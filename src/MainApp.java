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
        background(0,5);
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

        noStroke();
        fill(0);
        rect(0,0,width,height);

        translate(width/2, height/2);
        drawCircle(8, 1);

        fx.render().sobel().compose();
    }

    void drawCircle(float detail, float r){
        if(r < 0 || r > width){
            return;
        }

        float xFirst = 0;
        float yFirst = 0;

        for(float i = 0; i < 360f; i+= 360f/detail){
            int m = round(map(r, 0, width, 0,fft.specSize()));

            float hue = (frameCount/2f+abs(fft.getBand(m))*25)%255;
            float sat = 255;
            float br = 255;
            float alpha = 30;

            stroke(hue, sat, br, alpha);
            strokeWeight((fft.getBand(m)));

            float x0 = getXAtAngle(center,r,i);
            float y0 = getYAtAngle(center,r,i);
            float x1 = getXAtAngle(center,r,i+360f/detail);
            float y1 = getYAtAngle(center,r,i+360f/detail);

            //wrap last to first
            if(i == 0){
                xFirst = x0;
                yFirst = y0;
            }
            if(i+360f/detail > 360){
                x1=xFirst;
                y1=yFirst;
            }

            line(x0,y0,x1,y1);
        }
        drawCircle(detail, r+6);//map(mouseY,0, height, 2, 30));
    }

    public float getXAtAngle(PVector center, float radius, float angle) {
        return center.x + radius * cos(angle * PI / 180);
    }
    public float getYAtAngle(PVector center, float radius, float angle) {
        return center.y + radius * sin(angle * PI / 180);
    }

    void drawFFT(){
        stroke(255);
        for(int i = 0; i < fft.specSize(); i++)
        {
            stroke(255);
            strokeWeight(1);
            line(i, height, i, height - fft.getBand(i)*4);
        }
    }

}
