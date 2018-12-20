import java.io.Serializable;

public class person implements Serializable {
    private String name;
    private String password;
    private int ID;

    public person(){
        System.out.println("this is a default person!");
    }
    public person(String name, String password){

        System.out.println("i am a person!");
        this.name = name;
        this.password = password;
    }
    public String getName(){
        return name;
    }

    public void setName(String name){
       this.name = name;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
