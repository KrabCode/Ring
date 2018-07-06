import ch.bildspur.postfx.builder.PostFX;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PVector;

public class MainApp extends PApplet{

    Minim m;
    AudioInput in;
    FFT fft;
    BeatDetect bd;
    PVector center;
    PostFX fx;

    public static void main(String[] args) {
        PApplet.main("MainApp");
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {
        background(0);
        strokeCap(PROJECT);
        colorMode(HSB);
        m = new Minim(this);
        in = m.getLineIn();
        fft = new FFT(in.mix.size(), in.sampleRate());
        bd = new BeatDetect(in.mix.size(), in.sampleRate());
        center = new PVector(0,0);
        fx = new PostFX(this);
    }

    public void draw() {
        fft.forward(in.mix);
        bd.detect(in.mix);
        background(0, 5);
        stroke(255);
        translate(width/2, height/2);
        rotate(radians(frameCount/12f));
        drawCircle(6, 1);
        fx.render().compose();
    }

    void drawCircle(float detail, float r){
        if(r < 0 || r > width){
            return;
        }
        rotate(radians(frameCount/128f));
        for(float i = 0; i < 360f; i+= 360f/detail){
            int m = round(map(r, 0, width, 0,fft.getBandWidth()/2f));
            float hue = (frameCount+fft.getBand(m)*4)%255;
            float sat = 155;
            float br = 255;
            float alpha = 80;
            stroke(hue, sat, br, alpha);
            strokeWeight(fft.getBand(m)/2f);
            PVector a = getPointAtAngle(center, r, i);
            PVector b = getPointAtAngle(center, r, i+360f/detail);
            line(a.x,a.y,b.x,b.y);
        }
        drawCircle(detail, r+16);

    }

    public PVector getPointAtAngle(PVector center, float radius, float angle) {
        return new PVector(
                center.x + radius * cos(angle * PI / 180),
                center.y + radius * sin(angle * PI / 180)
        );
    }
}
