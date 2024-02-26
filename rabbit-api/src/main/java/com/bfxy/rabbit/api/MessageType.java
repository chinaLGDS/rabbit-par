package com.bfxy.rabbit.api;

/**
 * $MessageTyPE
 * @author nly
 */
public final class MessageType {

    /**
     *  迅速消息，不需要保障消息的可靠性，也不需要confirm确认
     */
    public static final  String RAPID  = "0";
    /**
     *  确认消息：不需要保障消息的可靠性，但是会做消息的confirm
     */
    public static final String CONFIRM = "1";

    /**
     *  可靠性消息：一定要保障消息的100%可靠性传递，不允许有任何消息的丢失
     *  PS:保障数据库和所发的小时是原子性的(最终一致的)
     */
    public static final String RELIANT = "2";


}
