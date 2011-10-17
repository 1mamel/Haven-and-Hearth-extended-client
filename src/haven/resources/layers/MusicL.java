package haven.resources.layers;

import haven.Resource;

import javax.sound.midi.Sequence;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
* // TODO: write javadoc
* Created by IntelliJ IDEA.
* Date: 18.05.11
* Time: 17:32
*
* @author Vlad.Rassokhin@gmail.com
*/
public class MusicL extends Layer {
    private transient javax.sound.midi.Sequence seq;

    public MusicL(final byte[] buf) {
        try {
            seq = javax.sound.midi.MidiSystem.getSequence(new ByteArrayInputStream(buf));
        } catch (javax.sound.midi.InvalidMidiDataException e) {
            throw (new Resource.LoadException("Invalid MIDI data"));
        } catch (IOException e) {
            throw (new Resource.LoadException(e));
        }
    }

    public void init() {
    }

    public Sequence getSeq() {
        return seq;
    }
}
