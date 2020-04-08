package Model;

public class UIPlacement {
    private int x;
    private int y;
    private int h;
    private int w;

    private String imagePath = "";

    static final UIPlacement trailers = new UIPlacement(997, 269, 20, 20);
    static final UIPlacement office = new UIPlacement(15, 500, 20, 20);

    UIPlacement(int x, int y, int h, int w)
    {
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
    }

    static UIPlacement combine(UIPlacement a, UIPlacement b)
    {
        return new UIPlacement(a.x + b.x, a.y + b.y, b.getH(), b.getW());
    }

    UIPlacement setDimensions(int width, int heigth)
    {
        w = width;
        h = heigth;
        return this;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public UIPlacement clone() {
        return new UIPlacement(x,y,h,w);
    }

    void setImagePath (String newPath)
    {
        this.imagePath = newPath;
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
