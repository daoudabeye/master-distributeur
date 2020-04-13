package ml.iks.md.service;

import ml.iks.md.events.callbacks.*;
import ml.iks.md.gateway.NumberRoute;
import ml.iks.md.gateway.OutboundServiceThread;
import ml.iks.md.gateway.ShutdownThread;
import ml.iks.md.hook.PreQueueHook;
import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.*;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.repositories.*;
import ml.ikslib.gateway.AbstractGateway;
import ml.ikslib.gateway.callback.events.DeliveryReportCallbackEvent;
import ml.ikslib.gateway.callback.events.InboundCallCallbackEvent;
import ml.ikslib.gateway.callback.events.InboundMessageCallbackEvent;
import ml.ikslib.gateway.callback.events.MessageSentCallbackEvent;
import ml.ikslib.gateway.groups.Group;
import ml.ikslib.gateway.helper.Common;
import ml.ikslib.gateway.message.AbstractMessage;
import ml.ikslib.gateway.message.MsIsdn;
import ml.ikslib.gateway.message.OutboundBinaryMessage;
import ml.ikslib.gateway.message.OutboundMessage;
import ml.ikslib.gateway.modem.Modem;
import ml.ikslib.gateway.modem.driver.serial.CommPortIdentifier;
import ml.ikslib.gateway.modem.driver.serial.SerialPort;
import ml.ikslib.gateway.routing.NumberRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.*;

@Service
public class GatewayService implements Serializable {

    @Autowired
    GatewayDefinitionRepository repository;

    public List<GatewayDefinition> paginate(Filter<GatewayDefinition> filter) {
        List<GatewayDefinition> commands = new ArrayList<>();
        Pageable pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize());
        if (filter.getParams().isEmpty()) {
            Page<GatewayDefinition> page = repository.findAll(pageable);
            commands = page.getContent();
        }

        return commands;
    }

    public void remove(GatewayDefinition command) {
        repository.delete(command);
    }

    public long count(Filter<GatewayDefinition> filter) {
        return repository.count();
    }

    public GatewayDefinition findById(Long id) {
        Optional<GatewayDefinition> c = repository.findById(id);
        return c.get();
    }

    public GatewayDefinition findById(String gatewayId) {
        Optional<GatewayDefinition> c = repository.findByGatewayId(gatewayId);
        return c.get();
    }

    public void update(GatewayDefinition command) {
        repository.save(command);
    }

    public void insert(GatewayDefinition command) {
        repository.save(command);
    }


    private static final Logger log = LoggerFactory.getLogger(GatewayService.class);
    public String profile = "*";

    @Autowired
    GatewayDefinitionRepository gatewayDefRepo;
    @Autowired
    NumberRouteDefinitionRepository numberRouteDefinitionRepo;
    @Autowired
    RoutingTableRepository routingTableRepository;
    @Autowired
    GroupDefinitionRepository groupDefinitionRepository;
    @Autowired
    InMessageRepository inMessageRepository;
    @Autowired
    OutMessageRepository outMessageRepository;
    @Autowired
    CallRepository callRepository;
    @Autowired
    DeviceInfoRepository deviceInfoRepository;
    @Autowired
    SimGroupRepository simGroupRepository;
    @Autowired
    SimRepository simRepository;

    public OutboundServiceThread outboundService;

    public OutboundServiceThread getOutboundServiceThread() {
        return this.outboundService;
    }

    public void startup() throws Exception {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        ml.ikslib.gateway.Service.getInstance().setServiceStatusCallback(new ServiceStatusCallback());
        ml.ikslib.gateway.Service.getInstance().setGatewayStatusCallback(new GatewayStatusCallback());
        ml.ikslib.gateway.Service.getInstance().setMessageSentCallback(new MessageSentCallback());
        ml.ikslib.gateway.Service.getInstance().setDequeueMessageCallback(new DequeueMessageCallback());
        ml.ikslib.gateway.Service.getInstance().setPreQueueHook(new PreQueueHook());
        ml.ikslib.gateway.Service.getInstance().setInboundMessageCallback(new InboundMessageCallback());
        ml.ikslib.gateway.Service.getInstance().setDeliveryReportCallback(new DeliveryReportCallback());
        ml.ikslib.gateway.Service.getInstance().setInboundCallCallback(new InboundCallCallback());
        ml.ikslib.gateway.Service.getInstance().setPreSendHook(new MessagePreSendHook());
        ml.ikslib.gateway.Service.getInstance().start();
        ml.ikslib.gateway.Service.getInstance().registerHttpRequestACL("/status", "0.0.0.0/0");
        loadGatewayDefinitions();
        loadGroups();
        loadNumberRoutes();
        loadRoutingTable();
        this.outboundService = new OutboundServiceThread();

//        MockGateway g1 = new MockGateway("G1", "Mock Gateway #1", new Capabilities(), 0, 0);
//        ml.ikslib.gateway.Service.getInstance().registerGateway(g1);
    }

    public void shutdown() {
        new ShutdownThread().start();
    }

    private void loadGatewayDefinitions() throws Exception {
        Collection<GatewayDefinition> gateways = getGatewayDefinitions(profile, true);
        for (GatewayDefinition gd : gateways) {
            startGateway(gd);
        }
    }

    public void startGateway(GatewayDefinition gd){

        if(ml.ikslib.gateway.Service.getInstance().getGatewayById(gd.getGatewayId()) != null){
            log.info("Gateway is running already " + gd.getGatewayId());
            return;
        }

        log.info("Registering gateway: " + gd.getGatewayId());
        try {
            String[] parms = new String[6];
            parms[0] = gd.getP0();
            parms[1] = gd.getP1();
            parms[2] = gd.getP2();
            parms[3] = gd.getP3();
            parms[4] = gd.getP4();
            parms[5] = gd.getP5();
            Object[] args = new Object[] { gd.getGatewayId(), parms };
            Class<?>[] argsClass = new Class[] { String.class, String[].class };
            Class<?> c = Class.forName(gd.getClassName());
            Constructor<?> constructor = c.getConstructor(argsClass);
            Modem g = (Modem) constructor.newInstance(args);
            if (!Common.isNullOrEmpty(gd.getSenderId()))
                g.setSenderAddress(new MsIsdn(gd.getSenderId()));
            g.setPriority(gd.getPriority());
            g.setMaxMessageParts(gd.getMaxMessageParts());
            g.setRequestDeliveryReport(gd.getRequestDeliveryReport());

            ml.ikslib.gateway.Service.getInstance().registerGateway(g);
            updateDeviceInformation(g);

            String imsi = g.getDeviceInformation().getImsi();
            if(imsi.startsWith("61001"))
                gd.setNetwork(Carrier.MALITEL);
            if(imsi.startsWith("61002"))
                gd.setNetwork(Carrier.ORANGE);
            if(imsi.startsWith("61003"))
                gd.setNetwork(Carrier.TELECEL);
            saveGateway(gd);
        } catch (Exception e) {
            log.error("Gateway " + gd.getGatewayId() + " did not start properly!", e);
        }
    }

    public boolean stopGateway(String gatewayId){
        AbstractGateway g = ml.ikslib.gateway.Service.getInstance().getGatewayById(gatewayId);
        if(g != null){
            log.info("Stopping gateway: " + g.getGatewayId());
            ml.ikslib.gateway.Service.getInstance().unregisterGateway(g);

            Optional<DeviceInfo> i = deviceInfoRepository.findByGateway(gatewayId);
            if(i.isPresent()){
                DeviceInfo d = i.get();
                d.setStatus("Stoped");
                deviceInfoRepository.save(d);
            }
        }

        return true;
    }


    public void deleteGateway(String gatewayId){
        if(stopGateway(gatewayId)){
            gatewayDefRepo.deleteByGatewayId(gatewayId);
        }
    }

    private void updateDeviceInformation(Modem modem) {
        Optional<DeviceInfo> info = deviceInfoRepository.findByGateway(modem.getGatewayId());
        if(info.isPresent()){
            DeviceInfo i = info.get();
            i.updateData(modem);
            deviceInfoRepository.save(i);
        }else{
            DeviceInfo i = new DeviceInfo(modem);
            deviceInfoRepository.save(i);
        }
    }

    public DeviceInfo loadInformation(String gatewayId){
        Optional<DeviceInfo> info = deviceInfoRepository.findByGateway(gatewayId);
        return info.isPresent() ? info.get(): new DeviceInfo();
    }

    public void removeDeviceInfo(String gatewayId){
        deviceInfoRepository.deleteByGateway(gatewayId);
    }

    private void loadGroups() throws Exception {
        Collection<GroupDefinition> groups = getGroupDefinitions(profile);
        for (GroupDefinition g : groups) {
            Group group = new Group(g.getName(), g.getDescription());
            for (GroupRecipientDefinition gr : g.getRecipients())
                group.addAddress(new MsIsdn(gr.getAddress()));
            ml.ikslib.gateway.Service.getInstance().getGroupManager().addGroup(group);
        }
    }

    private void loadNumberRoutes() throws Exception {
        NumberRouter nr = new NumberRouter();
        Collection<NumberRouteDefinition> routes = getNumberRouteDefinitions(profile);
        for (NumberRouteDefinition r : routes) {
            AbstractGateway g = ml.ikslib.gateway.Service.getInstance().getGatewayById(r.getGatewayId());
            if (g == null)
                log.error("Unknown gateway in number routes: " + r.getGatewayId());
            else
                nr.addRule(r.getAddressRegex(), g);
        }
//        if (nr.getRules().size() > 0)
//            ml.ikslib.gateway.Service.getInstance().setRouter(nr);
    }

    private void loadRoutingTable() throws Exception {
        NumberRoute nr = new NumberRoute();
        Collection<RoutingTable> routes = getRoutingtables(profile);
        for (RoutingTable r : routes) {
            List<String> ids = r.getIds();
            for (String id: ids){
                AbstractGateway g = ml.ikslib.gateway.Service.getInstance().getGatewayById(id);
                if (g == null)
                    log.error("Unknown gateway in number routes: " + id);
                //else
                //nr.addRule(id, g.getGatewayId());
//                nr.addRule(r.getAddressRegEx(), id);
            }
        }
        if (nr.getRules().size() > 0)
            ml.ikslib.gateway.Service.getInstance().setRouter(nr);
    }

    public Collection<GatewayDefinition> getGateways() throws Exception {
        return gatewayDefRepo.findAll();
    }

    public Collection<GatewayDefinition> getGatewayDefinitions(String profile) throws Exception {
        return gatewayDefRepo.findByProfile(profile);
    }

    public Collection<GatewayDefinition> getGatewayDefinitions(String profile, boolean enabled) throws Exception {
        return gatewayDefRepo.findByProfileAndEnabledOrderByPriorityDesc(profile, enabled);
    }

    public Collection<NumberRouteDefinition> getNumberRouteDefinitions(String profile) throws Exception {
        return numberRouteDefinitionRepo.findByProfile(profile);
    }

    public Collection<RoutingTable> getRoutingtables(String profile) throws Exception {
        return routingTableRepository.findByProfile(profile);
    }

    public Collection<GroupDefinition> getGroupDefinitions(String profile) throws Exception {
        return groupDefinitionRepository.findByProfile(profile);
    }

    public void setMessageStatus(OutboundMessage message, OutboundMessage.SentStatus status) throws Exception {
        Optional<OutMessage> out = outMessageRepository.findByMessageId(message.getId());

        if(out.isPresent()){
            OutMessage sms = out.get();
            sms.setSentStatus(status.toShortString());

            outMessageRepository.save(sms);
        }
    }

    public void saveInboundCall(InboundCallCallbackEvent event) throws Exception {
        Call call = new Call(event.getGatewayId(), new Timestamp(event.getDate().getTime()), event.getMsisdn().getAddress());
        callRepository.save(call);
    }

    public void saveDeliveryReport(DeliveryReportCallbackEvent event) throws Exception {
//        Optional<OutMessage> out = outMessageRepository.findFirstByAddressAndOperatorMessageIdAndGatewayId(
//                event.getMessage().getRecipientAddress().getAddress(), event.getMessage().getOriginalOperatorMessageId(),
//                event.getMessage().getGatewayId());
        Optional<OutMessage> out = outMessageRepository.findByOperatorMessageIdAndGatewayId(
                event.getMessage().getOriginalOperatorMessageId(), event.getMessage().getGatewayId());

//        log.info("Address ! ...." + event.getMessage().getRecipientAddress().getAddress().substring(3));
        if(out.isPresent()){
            OutMessage sms = out.get();
            sms.setDeliveryStatus(event.getMessage().getDeliveryStatus().toShortString());
            sms.setDeliveryDate(new Timestamp(event.getMessage().getOriginalReceivedDate().getTime()));
            outMessageRepository.save(sms);
        }
    }

    /**
     *
     * @param event
     * @return
     * @throws Exception
     */
    public InMessage saveInboundMessage(InboundMessageCallbackEvent event) throws Exception {
        InMessage sms = new InMessage();
        sms.setAddress(event.getMessage().getOriginatorAddress().getAddress());
        sms.setEncoding(event.getMessage().getEncoding().toShortString());

        Optional<Sim> sim = simRepository.findByGateway(event.getMessage().getGatewayId());
        if(sim.isPresent()){
            sms.setSim(sim.get());
            sms.setCarrier(sim.get().getOperator());
        }

        switch (event.getMessage().getEncoding()) {
            case Enc7:
            case EncUcs2:
                sms.setText(event.getMessage().getPayload().getText());
                break;
            case Enc8:
                sms.setText(Common.bytesToString(event.getMessage().getPayload().getBytes()));
                break;
            case EncCustom:
                throw new UnsupportedOperationException();
        }
        sms.setMessageDate(new Timestamp(event.getMessage().getSentDate().getTime()));
        sms.setReceiveDate(new Timestamp(event.getMessage().getCreationDate().getTime()));
        sms.setGatewayId(event.getMessage().getGatewayId());
        sms.setMemIndex(event.getMessage().getMemIndex());

        return inMessageRepository.save(sms);
    }

    public OutMessage saveOutboundMessage(OutboundMessage outboundMessage) throws Exception {
        OutMessage outMessage = new OutMessage(outboundMessage);
        return outMessageRepository.save(outMessage);
    }

    public void markMessageSent(MessageSentCallbackEvent event) throws Exception {
//        event.getMessage().getId();
        Optional<OutMessage> out = outMessageRepository.findByOperatorMessageId(event.getMessage().getOperatorMessageId());

        log.info("Mark as sent :" + out);

        if(out.isPresent()){
            log.info("Mark Is Present ");
            OutMessage msg = out.get();
            msg.setSentStatus(event.getMessage().getSentStatus().toShortString());
            msg.setSentDate(new Timestamp((event.getMessage().getSentStatus() == OutboundMessage.SentStatus.Sent
                    ? event.getMessage().getSentDate().getTime() : 0)));
            msg.setGatewayId(event.getMessage().getSentStatus() == OutboundMessage.SentStatus.Sent
                    ? event.getMessage().getGatewayId() : "");
            msg.setOperatorMessageId(event.getMessage().getSentStatus() == OutboundMessage.SentStatus.Sent
                    ? event.getMessage().getOperatorMessageId() : "");

            outMessageRepository.save(msg);
        }
    }

    public Collection<OutboundMessage> getMessagesToSend() throws Exception {
        Collection<OutMessage> outs = outMessageRepository.findBySentStatus(OutboundMessage.SentStatus.Unsent.name());
        Collection<OutboundMessage> messages = new LinkedList<OutboundMessage>();
        for(OutMessage sms: outs){
            if (!Common.isNullOrEmpty(sms.getMessageId())) {
                OutboundMessage message;
                String senderId = sms.getSenderAddress();
                String recipient = sms.getAddress();
                String text = sms.getText();
                String encoding = sms.getEncoding();
                if (AbstractMessage.Encoding.getEncodingFromShortString(encoding) == AbstractMessage.Encoding.Enc7) {
                    message = new OutboundMessage(new MsIsdn(recipient), text);
                } else if (AbstractMessage.Encoding.getEncodingFromShortString(encoding) == AbstractMessage.Encoding.Enc8) {
                    message = new OutboundBinaryMessage(new MsIsdn(recipient), Common.stringToBytes(text));
                } else if (AbstractMessage.Encoding.getEncodingFromShortString(encoding) == AbstractMessage.Encoding.EncUcs2) {
                    message = new OutboundMessage(new MsIsdn(recipient), text);
                    message.setEncoding(AbstractMessage.Encoding.EncUcs2);
                } else {
                    // TODO: ENC-CUSTOM
                    message = new OutboundMessage(new MsIsdn(recipient), text);
                }
                message.setEncoding(AbstractMessage.Encoding.getEncodingFromShortString(encoding));
                message.setId(sms.getMessageId());
                if (!Common.isNullOrEmpty(senderId))
                    message.setOriginatorAddress(new MsIsdn(senderId));
                message.setPriority(sms.getPriority());
                message.setRequestDeliveryReport(sms.getRequestDeliveryReport());
                message.setFlashSms(sms.getFlashSms());
                // if (!isGroupMessage(message)) Service.getInstance().queue(message);
                messages.add(message);
            }
        }
        return messages;
    }

    public Collection<String> portList(){
        List<String> ports = new ArrayList<>();
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        while(portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            ports.add(portId.getName());
        }
        return ports;
    }

    public void saveGateway(GatewayDefinition gd) {
        gatewayDefRepo.save(gd);
    }

    public Optional<GatewayDefinition> find(Long id) {
        return gatewayDefRepo.findById(id);
    }

    public Optional<Sim> findSim(Long id) {
        return simRepository.findById(id);
    }

    public void saveSim(Sim sim) {
        simRepository.save(sim);
    }

    public String testPort(String name) throws Exception{
        int bauds[] = { 9600, 14400, 19200, 28800, 33600, 38400, 56000, 57600, 115200 };
        CommPortIdentifier portId = null;
        try {
            portId = CommPortIdentifier.getPortIdentifier(name);
        } catch (Exception e){
            return "NoSuchPortException";
        }

        String status = "";

        if(portId == null)
            status = "Invalid port name";

        if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            System.out.println(String.format("====== Found port: %-5s", portId.getName()));
            for (int i = 0; i < bauds.length; i++) {
                SerialPort serialPort = null;
                InputStream inStream = null;
                OutputStream outStream = null;
                System.out.print(String.format(">> Trying at %6d...", bauds[i]));
                try {
                    int c;
                    String response;
                    serialPort = portId.open("SMSLibCommTester", 5000);
                    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
                    serialPort.setSerialPortParams(bauds[i], SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    inStream = serialPort.getInputStream();
                    outStream = serialPort.getOutputStream();
                    serialPort.enableReceiveTimeout(1000);
                    c = inStream.read();
                    while (c != -1)
                        c = inStream.read();
                    outStream.write('A');
                    outStream.write('T');
                    outStream.write('\r');
                    Thread.sleep(1000);
                    response = "";
                    StringBuilder sb = new StringBuilder();
                    c = inStream.read();
                    while (c != -1) {
                        sb.append((char) c);
                        c = inStream.read();
                    }
                    response = sb.toString();
                    if (response.indexOf("OK") >= 0) {
                        try {
                            System.out.print("  Getting Info...");
                            outStream.write('A');
                            outStream.write('T');
                            outStream.write('+');
                            outStream.write('C');
                            outStream.write('G');
                            outStream.write('M');
                            outStream.write('M');
                            outStream.write('\r');
                            response = "";
                            c = inStream.read();
                            while (c != -1) {
                                response += (char) c;
                                c = inStream.read();
                            }
                            System.out.println(" Found: " + response.replaceAll("\\s+OK\\s+", "")
                                    .replaceAll("\n", "").replaceAll("\r", ""));
                            status = "Ready["+bauds[i]+"]";
                        } catch (Exception e) {
                            System.out.println("No device...");
                            status = "No device...";
                        }
                    } else {
                        System.out.println("No device...");
                        status = "No device...";
                    }
                } catch (Exception e) {
                    System.out.print("No device...");
                    status = "No device...";
                    Throwable cause = e;
                    while (cause.getCause() != null)
                        cause = cause.getCause();
                    System.out.println(" (" + cause.getMessage() + ")");
                } finally {
                    if (inStream != null)
                        inStream.close();
                    if (outStream != null)
                        outStream.close();
                    if (serialPort != null)
                        serialPort.close();
                }
            }
        }
        return status;
    }

    public Optional<SimGroup> findSimGroup(String name){
        return simGroupRepository.findByName(name);
    }

    public void save(SimGroup simGroup){
        simGroupRepository.save(simGroup);
    }

    public Collection<SimGroup> findAllSimGroup(){
        return  simGroupRepository.findAll();
    }

    public void deleteSimGroup(SimGroup g) {
        simGroupRepository.delete(g);
    }

    public Collection<Sim> findSims(SimGroup g) {
        return simRepository.findByGroup(g);
    }

    public Collection<Sim> findAllSims() {
        return simRepository.findAll();
    }

    public void saveMessage(OutboundMessage out) {
        outMessageRepository.save(new OutMessage(out));
    }

    public AbstractGateway getGatewayById(String gatewayId) {
        return ml.ikslib.gateway.Service.getInstance().getGatewayById(gatewayId);
    }

    public void cleanGtw(String gatewayId) throws Exception {
        ml.ikslib.gateway.Service.getInstance().getGatewayById(gatewayId).cleanMemory();
    }
}
