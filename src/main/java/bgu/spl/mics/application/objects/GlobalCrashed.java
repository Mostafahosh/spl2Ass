package bgu.spl.mics.application.objects;

public class GlobalCrashed {
    private static GlobalCrashed instance;
    private boolean crash =  false;
    public static GlobalCrashed getInstance(){
        if(instance==null){
            instance=new GlobalCrashed();
        }
        return instance;
    }

    public boolean getCrahs(){return crash;}
    public void setCrash(){crash = true;}

}
