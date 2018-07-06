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

    public static void main(String[] args) {
        PApplet.main("MainApp");
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {
        background(0);
        strokeCap(ROUND);
        colorMode(HSB);
        m = new Minim(this);
        in = m.getLineIn();
        fft = new FFT(in.mix.size(), in.sampleRate());
        bd = new BeatDetect(in.mix.size(), in.sampleRate());
        center = new PVector(0,0);
    }

    public void draw() {
        fft.forward(in.mix);
        bd.detect(in.mix);
        background(0, 5);
        stroke(255);
        strokeWeight(1);

        translate(width/2, height/2);
        drawCircle(6, 1);
    }

    void drawCircle(float detail, float r){
        if(r < 0 || r > width){
            return;
        }
        for(float i = 0; i < 360f; i+= 360f/detail){
            int m = round(map(r, 0, width, 0,fft.getBandWidth()));
            int h = round(map(r, 0, width, 0,bd.detectSize()));

            float hue =  ((frameCount/2f+fft.getBand(m))*5)%255;
            float sat = 60;
            float br = 180;
            float alpha = 80;

            if(h < bd.detectSize() && bd.isOnset(h)){
                sat = 255;
                br = 255;
                alpha = 100;
            }

            stroke(hue, sat, br, alpha);
            strokeWeight(fft.getBand(m)/2);
            PVector a = getPointAtAngle(center, r, i);
            PVector b = getPointAtAngle(center, r, i+360f/detail);
            line(a.x,a.y,b.x,b.y);
        }
        drawCircle(detail, r+10);

    }

    public PVector getPointAtAngle(PVector center, float radius, float angle) {
        return new PVector(center.x + radius * cos(angle * PI / 180), center.y + radius * sin(angle * PI / 180));
    }
}
