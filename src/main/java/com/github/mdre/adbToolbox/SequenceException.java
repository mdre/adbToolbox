/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.github.mdre.adbToolbox;

/**
 *
 * @author mdre
 */
public class SequenceException extends RuntimeException {

    /**
     * Creates a new instance of <code>sqException</code> without detail message.
     */
    public SequenceException() {
        super("sequence not found!");
    }

    /**
     * Constructs an instance of <code>sqException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public SequenceException(String msg) {
        super(msg);
    }
}
