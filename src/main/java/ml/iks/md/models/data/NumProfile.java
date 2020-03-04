package ml.iks.md.models.data;

public enum NumProfile {
    CF, RV, GR, MST, RVI, ISAGO, MOBICASH;

    /**
     *
     * @param val
     * @return
     */
    public static NumProfile getFrom(int val){
        if(val == 1)
            return NumProfile.CF;
        if(val == 2)
            return NumProfile.RV;
        if(val == 3)
            return NumProfile.RVI;
        if(val == 4)
            return NumProfile.GR;
        if(val == 5)
            return NumProfile.MST;
        if(val == 6)
            return NumProfile.ISAGO;
        if(val == 7)
            return NumProfile.MOBICASH;
        return null;
    }

    public static NumProfile getParent(NumProfile profileToRecharge){
        switch (profileToRecharge){
            case CF:
                return NumProfile.RV;
            case RV:
                return NumProfile.GR;
            case GR:
                return NumProfile.MST;
            case MOBICASH:
                return NumProfile.MOBICASH;
        }
        return null;
    }

    public static NumProfile getChild(NumProfile childe){
        switch (childe){
            case MST:
                return NumProfile.GR;
            case GR:
                return NumProfile.RV;
            case RV:
                return NumProfile.CF;
            case MOBICASH:
                return NumProfile.MOBICASH;
        }
        return null;
    }



    @Override
    public String toString() {
        return this.name();
    }
}
