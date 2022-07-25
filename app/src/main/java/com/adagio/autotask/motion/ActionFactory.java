package com.adagio.autotask.motion;

public class ActionFactory {

    public static Action createAction(String actionName) {
        final ActionType actionType = ActionType.valueOf(actionName);
        try {
            return actionType.getTypeClass().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Action creation error");
    }
}
