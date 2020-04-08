package ViewController;

public enum Act {
    MOVE, ACT, REHEARSE, UPGRADE, TAKE_ROLE, END_TURN, BAD_ACTION;

    public String getMessage()
    {
        String retVal = "Error";

        switch(this)
        {
            case MOVE: {
                retVal = "Move";
                break;
            }
            case ACT: {
                retVal = "Act";
                break;
            }
            case REHEARSE: {
                retVal = "Rehearse";
                break;
            }
            case UPGRADE: {
                retVal = "Upgrade";
                break;
            }
            case TAKE_ROLE:{
                retVal = "Take a role";
                break;
            }
            case END_TURN:{
                retVal = "End turn";
                break;
            }
        }
        return retVal;
    }
}


