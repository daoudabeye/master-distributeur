package ml.iks.md.events.listeners;

import ml.iks.md.events.InscriptionEvent;
import ml.iks.md.events.callbacks.IInscriptionCallback;

public class InscriptionListener implements IInscriptionCallback {

    @Override
    public boolean process(InscriptionEvent event) {
        try{
            event.getInscriptionOperation().execute();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return true;
        }
    }
}
