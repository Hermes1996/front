import java.util.Optional;

public class Test
{
    public static void main(String[] args)
    {
        String platformOrderId = "Y27022875_CP_3652";
        if (platformOrderId !=null && platformOrderId.length() > 16 && platformOrderId.contains("_CP_"))
        {
            platformOrderId = platformOrderId.substring(0, platformOrderId.indexOf("_CP_"));
        }
        System.out.println(platformOrderId);
    }

}
