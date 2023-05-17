package minesweeper;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl.Type;

public class Sound implements AutoCloseable {
    private boolean released = false;
    private AudioInputStream stream = null;
    private Clip clip = null;
    private FloatControl volumeControl = null;
    private boolean playing = false;

    public Sound(File f) {
        try {
            this.stream = AudioSystem.getAudioInputStream(f);
            this.clip = AudioSystem.getClip();
            this.clip.open(this.stream);
            this.clip.addLineListener(new Sound.Listener());
            this.volumeControl = (FloatControl)this.clip.getControl(Type.MASTER_GAIN);
            this.released = true;
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException var3) {
            var3.printStackTrace();
            this.released = false;
            this.close();
        }

    }

    public boolean isReleased() {
        return this.released;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void play(boolean breakOld) {
        if (this.released) {
            if (breakOld) {
                this.clip.stop();
                this.clip.setFramePosition(0);
                this.clip.start();
                this.playing = true;
            } else if (!this.isPlaying()) {
                this.clip.setFramePosition(0);
                this.clip.start();
                this.playing = true;
            }
        }

    }

    public void play() {
        this.play(true);
    }

    public void stop() {
        if (this.playing) {
            this.clip.stop();
        }

    }

    public void close() {
        if (this.clip != null) {
            this.clip.close();
        }

        if (this.stream != null) {
            try {
                this.stream.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public void setVolume(float x) {
        if (x < 0.0F) {
            x = 0.0F;
        }

        if (x > 1.0F) {
            x = 1.0F;
        }

        float min = this.volumeControl.getMinimum();
        float max = this.volumeControl.getMaximum();
        this.volumeControl.setValue((max - min) * x + min);
    }

    public float getVolume() {
        float v = this.volumeControl.getValue();
        float min = this.volumeControl.getMinimum();
        float max = this.volumeControl.getMaximum();
        return (v - min) / (max - min);
    }

    public void join() {
        if (this.released) {
            synchronized(this.clip) {
                try {
                    while(this.playing) {
                        this.clip.wait();
                    }
                } catch (InterruptedException var4) {
                }

            }
        }
    }

    public static Sound playSound(String path) {
        File f = new File(path);
        Sound snd = new Sound(f);
        snd.play();
        return snd;
    }

    private class Listener implements LineListener {
        private Listener() {
        }

        public void update(LineEvent ev) {
            if (ev.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                Sound.this.playing = false;
                synchronized(Sound.this.clip) {
                    Sound.this.clip.notify();
                }
            }

        }
    }
}
