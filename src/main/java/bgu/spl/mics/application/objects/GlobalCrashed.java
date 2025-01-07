package bgu.spl.mics.application.objects;

public class GlobalCrashed {
    private static GlobalCrashed instance;
    private boolean crash =  false;
    private boolean stop = false;
    public static GlobalCrashed getInstance(){
        if(instance==null){
            instance=new GlobalCrashed();
        }
        return instance;
    }

    public boolean getStop(){return stop;}
    public void setStop(){stop = true;}
    public boolean getCrahs(){return crash;}
    public void setCrash(){crash = true;}

}
