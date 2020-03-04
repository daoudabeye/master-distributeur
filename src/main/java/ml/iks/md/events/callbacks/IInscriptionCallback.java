package ml.iks.md.events.callbacks;

import ml.iks.md.events.InscriptionEvent;

public interface IInscriptionCallback {
    public boolean process(InscriptionEvent event);
}
