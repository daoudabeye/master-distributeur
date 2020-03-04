package ml.iks.md.infra.model;

import ml.iks.md.models.Command;

public class CommandBuilder {
    String cmd;
    public CommandBuilder(Command cmd){this.cmd = cmd.getPattern();}

    public CommandBuilder(String cmd){this.cmd = cmd;}

    public String build(){
        return cmd;
    }

    public CommandBuilder withNumero(String numero){
        this.cmd = cmd.replaceAll("numero", numero);
        return this;
    }

    public CommandBuilder withAmount(String amount){
        this.cmd = this.cmd.replaceAll("montant", amount);
        return this;
    }

    public CommandBuilder withAmount(double amount){
        this.cmd = this.cmd.replaceAll("montant", String.valueOf((int) amount));
        return this;
    }

    public CommandBuilder withAmount(int amount){
        this.cmd = this.cmd.replaceAll("montant", String.valueOf(amount));
        return this;
    }

    public CommandBuilder withFee(String fee){
        this.cmd = this.cmd.replaceAll("frais", fee);
        return this;
    }

    public CommandBuilder withFee(double fee){
        this.cmd = this.cmd.replaceAll("frais", String.valueOf((int) fee));
        return this;
    }

    public CommandBuilder withFee(int fee){
        this.cmd = this.cmd.replaceAll("frais", String.valueOf(fee));
        return this;
    }

    public CommandBuilder withId(String id){
        this.cmd = this.cmd.replaceAll("id", id);
        return this;
    }

    public CommandBuilder withNumClient(String client){
        this.cmd = this.cmd.replaceAll("numClient", client);
        return this;
    }

}
