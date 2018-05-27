/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsavant.battletech;

/**
 *
 * @author jgroc
 */
public interface EventListener {
    public void handleEvent(String eventType, String eventName, Object relatedValue);
}
