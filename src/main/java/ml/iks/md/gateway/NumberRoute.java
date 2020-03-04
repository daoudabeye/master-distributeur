package ml.iks.md.gateway;

import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.repositories.GatewayDefinitionRepository;
import ml.iks.md.repositories.RoutingTableRepository;
import ml.iks.md.util.BeanLocator;
import ml.ikslib.gateway.AbstractGateway;
import ml.ikslib.gateway.message.OutboundMessage;
import ml.ikslib.gateway.routing.AbstractRouter;
import ml.ikslib.gateway.ussd.USSDRequest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberRoute extends AbstractRouter {

    Collection<RoutingTable> table = new ArrayList<>();

    public Collection<RoutingTable> getRules() {
        this.table = BeanLocator.find(RoutingTableRepository.class).findAll();
        return this.table;
    }

    public void addRule(String addressRegEx, String gatewayId) {
        Optional<GatewayDefinition> gd = BeanLocator.find(GatewayDefinitionRepository.class).findByGatewayId(gatewayId);
        if (gd.isPresent()) {
            Optional<RoutingTable> r = BeanLocator.find(RoutingTableRepository.class).findByAddressRegEx(addressRegEx);
            if (r.isPresent()) {
                RoutingTable route = r.get();
                route.addGateway(gd.get());
                BeanLocator.find(RoutingTableRepository.class).save(route);
            } else {
                RoutingTable routingTable = new RoutingTable(addressRegEx, gd.get());
                BeanLocator.find(RoutingTableRepository.class).save(routingTable);
            }
            getRules();
        }
    }

    public void deleteRule(String pattern, String gatewayId) {
        Optional<RoutingTable> r = BeanLocator.find(RoutingTableRepository.class).findByAddressRegEx(pattern);
        if (r.isPresent()) {
            if (StringUtils.isEmpty(gatewayId)) {
                Optional<GatewayDefinition> gd = BeanLocator.find(GatewayDefinitionRepository.class).findByGatewayId(gatewayId);
                if (gd.isPresent()) {
                    RoutingTable table = r.get();
                    table.removeGateway(gd.get());
                    BeanLocator.find(RoutingTableRepository.class).save(table);
                } else {
                    BeanLocator.find(RoutingTableRepository.class).delete(r.get());
                }
            }
            getRules();
        }
    }

    @Override
    public Collection<AbstractGateway> customRoute(OutboundMessage message, Collection<AbstractGateway> gateways) {
        Collection<AbstractGateway> candidates = new ArrayList<>();
        if (getRules().size() != 0) {
            for (RoutingTable rx : table) {
                Pattern p = Pattern.compile(rx.getAddressRegEx());
                Matcher m = p.matcher(message.getRecipientAddress().getAddress());
                if (m.matches()) {
                    List<String> ids = rx.getIds();
                    for (AbstractGateway ag : gateways) {
                        if (ids.contains(ag.getGatewayId()))
                            candidates.add(ag);
                    }
                }
            }
        }
        return candidates;
    }

    @Override
    public Collection<AbstractGateway> customRoute(USSDRequest ussdRequest, Collection<AbstractGateway> gateways) {
        Collection<AbstractGateway> candidates = new ArrayList<>();
        if (!StringUtils.isEmpty(ussdRequest.getGatewayId())) {
            for (AbstractGateway ag : gateways) {
                if (ussdRequest.getGatewayId().equals(ag.getGatewayId()))
                    candidates.add(ag);
            }
        } else if (getRules().size() != 0) {
            for (RoutingTable rx : table) {
                Pattern p = Pattern.compile(rx.getAddressRegEx());
                Matcher m = p.matcher(ussdRequest.getContent());
                if (m.matches()) {
                    List<String> ids = rx.getIds();
                    for (AbstractGateway ag : gateways) {
                        if (ids.contains(ag.getGatewayId()))
                            candidates.add(ag);
                    }
                }
            }
        }
        return candidates;
    }
}
