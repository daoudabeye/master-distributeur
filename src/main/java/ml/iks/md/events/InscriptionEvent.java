package ml.iks.md.events;

import ml.iks.md.models.InMessage;
import ml.iks.md.models.InscriptionOperation;

public class InscriptionEvent extends AirtimeAppEvent {

    InMessage in;
    InscriptionOperation inscriptionOperation;

    public InscriptionEvent(InMessage in, InscriptionOperation inscriptionOperation) {
        this.in = in;
        this.inscriptionOperation = inscriptionOperation;
    }

    public InMessage getIn() {
        return in;
    }

    public InscriptionOperation getInscriptionOperation() {
        return inscriptionOperation;
    }
}
