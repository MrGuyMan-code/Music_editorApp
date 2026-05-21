/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package music.editor.grid;

public class NoteInfo {

    private int startBeat;
    private int duration;

    public NoteInfo(int startBeat, int duration) {

        this.startBeat = startBeat;
        this.duration = duration;
    }

    public int getStartBeat() {
        return startBeat;
    }

    public int getDuration() {
        return duration;
    }
    
}