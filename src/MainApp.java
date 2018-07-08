import ch.bildspur.postfx.builder.PostFX;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;

public class MainApp extends PApplet{

    Minim m;
    AudioInput in;
    FFT fft;
    BeatDetect bd;
    PVector center;
    PostFX fx;
    PeasyCam cam;
    boolean threeD = true;

    public static void main(String[] args) {
        PApplet.main("MainApp");
    }

    public void settings() {
//        fullScreen(P2D, 1);
        fullScreen(P3D);
//        size(800,600,P2D);
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
        if(threeD){
            cam = new PeasyCam(this,g,500);
        }
        fx = new PostFX(this);
    }

    public void draw() {
        fft.forward(in.mix);
        bd.detect(in.mix);

        background(0);
        lights();

        if(!threeD){
            translate(width/2,height/2);
        }
        drawCircle(12, 1);

        if(threeD){cam.beginHUD();}
        fx.render().sobel().compose();
        if(threeD){cam.endHUD();}
    }

    void drawCircle(float detail, float r){
        float scl = 1f;

        if(r < 0 || r > fft.specSize()*scl){
            return;
        }

        float xFirst = 0;
        float yFirst = 0;

        for(float i = 0; i < 360f; i+= 360f/detail){
            pushMatrix();

            float my = map(mouseY, 0, height, 1, 16);
            int b = round(r/scl/my);

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

            float hue = (frameCount/8f+fft.getBand(b)*5)%255;
            float sat = 255;
            float br = fft.getBand(b)*5;
            float alpha = 80;

            if(threeD){
                fill(hue, sat, br, alpha);
                noStroke();
                translate(x0,y0, 0);
                box(10,10,br);
            }else{
                noFill();
                stroke(hue, sat, br, alpha);
                strokeWeight(fft.getBand(b));
                line(x0, y0, x1, y1);
            }
            popMatrix();
        }

        drawCircle(detail, r+15);
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
