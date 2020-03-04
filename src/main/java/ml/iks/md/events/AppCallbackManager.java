package ml.iks.md.events;

import ml.iks.md.events.callbacks.IInscriptionCallback;
import ml.iks.md.events.callbacks.IPaymentMessageCallback;
import ml.iks.md.models.InMessage;
import ml.iks.md.models.InscriptionOperation;
import ml.iks.md.models.Payment;
import ml.ikslib.gateway.core.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class AppCallbackManager {

    static Logger logger = LoggerFactory.getLogger(AppCallbackManager.class);
    LinkedBlockingQueue<AirtimeAppEvent> eventQueue = new LinkedBlockingQueue<>();

    IPaymentMessageCallback paymentMessageCallback;
    IInscriptionCallback inscriptionCallback;

    AppCallbackManagerDispatcher dispatcher = null;

    boolean shouldCancel = false;

    public boolean registerPaymentMessageEvent(InMessage message, Payment payment) {
        return (this.paymentMessageCallback != null && this.eventQueue.add(new PaymentMessageEvent(message, payment)));
    }

    public boolean registerInscriptionEvent(InMessage message, InscriptionOperation inscriptionOperation) {
        return (this.inscriptionCallback != null && this.eventQueue.add(new InscriptionEvent(message, inscriptionOperation)));
    }

    public AppCallbackManager(){}

    public void start() {
        logger.debug("Starting...");
        this.dispatcher = new AppCallbackManagerDispatcher();
        this.dispatcher.start();
    }

    public void stop() {
        logger.debug("Cancelling!");
        this.shouldCancel = true;
        this.dispatcher.interrupt();
        try {
            this.dispatcher.join();
        } catch (InterruptedException e) {
            logger.warn("Interrupted!", e);
        }
        this.dispatcher = null;
    }

    public class AppCallbackManagerDispatcher extends Thread {
        public AppCallbackManagerDispatcher() {
            setName("Callback Manager Dispatcher");
            setDaemon(false);
        }

        @Override
        public void run() {
            logger.debug("Started!");
            while (!AppCallbackManager.this.shouldCancel) {
                try {

                    AirtimeAppEvent ev = AppCallbackManager.this.eventQueue.poll(Settings.callbackDispatcherQueueTimeout,
                            TimeUnit.MILLISECONDS);
                    if (ev != null) {
                        boolean consumed = false;
                        boolean handlerFound = false;
                        if (ev instanceof PaymentMessageEvent) {
                            if (AppCallbackManager.this.paymentMessageCallback != null) {
                                try {
                                    handlerFound = true;
                                    consumed = AppCallbackManager.this.paymentMessageCallback
                                            .process((PaymentMessageEvent) ev);
                                } catch (Exception e) {
                                    logger.error("Error in ServiceStatusCallback!", e);
                                }
                            }

                            if (handlerFound && !consumed)
                                AppCallbackManager.this.eventQueue.put(ev);
                        }

                        if (ev instanceof InscriptionEvent) {
                            if (AppCallbackManager.this.inscriptionCallback != null) {
                                try {
                                    handlerFound = true;
                                    consumed = AppCallbackManager.this.inscriptionCallback
                                            .process((InscriptionEvent) ev);
                                } catch (Exception e) {
                                    logger.error("Error in InscriptionEvent!", e);
                                }
                            }

                            if (handlerFound && !consumed)
                                AppCallbackManager.this.eventQueue.put(ev);
                        }
                    }

                    sleep(Settings.callbackDispatcherYield);
                } catch (InterruptedException e1) {
                    if (!AppCallbackManager.this.shouldCancel)
                        logger.error("Interrupted!", e1);
                } catch (Exception e2) {
                    logger.error("Unhandled exception!", e2);
                }
            }
            logger.debug("Stopped!");
        }
    }

    public IPaymentMessageCallback getPaymentMessageCallback() {
        return paymentMessageCallback;
    }

    public void setPaymentMessageCallback(IPaymentMessageCallback paymentMessageCallback) {
        this.paymentMessageCallback = paymentMessageCallback;
    }

    public void setInscriptionCallback(IInscriptionCallback inscriptionCallback) {
        this.inscriptionCallback = inscriptionCallback;
    }
}
