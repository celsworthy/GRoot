package celuk.groot.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

public class MachineDetails
{
    public static MachineDetails defaultDetails;
    public static Map<String, MachineDetails> machineDetailsMap;
    static {
        defaultDetails = new MachineDetails("machine.robox",
                                            "url(\"/image/logo-robox-black-20pc.png\")",
                                            "/image/logo-robox-black-20pc.png",
                                            "/image/logo-robox-white-20pc.png",
                                            "url(\"/image/machine-robox-black.png\")",
                                            "url(\"/image/machine-robox-white.png\")");
        
        machineDetailsMap = new HashMap<>();
        machineDetailsMap.put("RBX01", defaultDetails);
        machineDetailsMap.put("RBX02", defaultDetails);
        machineDetailsMap.put("RBX10",
                              new MachineDetails("machine.roboxPro",
                                                 "url(\"/image/logo-roboxpro-black-20pc.png\")",
                                                 "/image/logo-roboxpro-black-20pc.png",
                                                 "/image/logo-roboxpro-white-20pc.png",
                                                 "url(\"/image/machine-roboxpro-black.png\")",
                                                 "url(\"/image/machine-roboxpro-white.png\")"));
    }
    
    public String model;
    public String idleIcon;
    public String statusIconLight;
    public String statusIconDark;
    public String machineIconLight;
    public String machineIconDark;

    public MachineDetails()
    {
        // Jackson deserialization
    }

    public MachineDetails(String model,
                          String idleIcon,
                          String statusIconLight,
                          String statusIconDark,
                          String machineIconLight,
                          String machineIconDark)
    {
        this.model = model;
        this.idleIcon = idleIcon;
        this.statusIconLight = statusIconLight;
        this.statusIconDark = statusIconDark;
        this.machineIconLight = machineIconLight;
        this.machineIconDark = machineIconDark;
    }

    @JsonProperty
    public String getModel()
    {
        return model;
    }

    @JsonProperty
    public void setModel(String model)
    {
        this.model = model;
    }

    @JsonProperty
    public String getIdleIcon()
    {
        return idleIcon;
    }

    @JsonProperty
    public void getIdleIcon(String idleIcon)
    {
        this.idleIcon = idleIcon;
    }

    @JsonProperty
    public String getStatusIconLight()
    {
        return statusIconLight;
    }

    @JsonProperty
    public void setStatusIconLight(String statusIconLight)
    {
        this.statusIconLight = statusIconLight;
    }

    @JsonProperty
    public String getStatusIconDark()
    {
        return statusIconDark;
    }

    @JsonProperty
    public void setStatusIconDark(String statusIconDark)
    {
        this.statusIconDark = statusIconDark;
    }

    @JsonIgnore
    public String getStatusIcon(String webColour)
    {
        return getComplimentaryOption(webColour, statusIconLight, statusIconDark);
    }

    @JsonProperty
    public String getMachineIconDark()
    {
        return machineIconDark;
    }

    @JsonProperty
    public void setMachineIconDark(String machineIconDark)
    {
        this.machineIconDark = machineIconDark;
    }

    @JsonProperty
    public String getMachineIconLight()
    {
        return machineIconLight;
    }

    @JsonProperty
    public void setMachineIconLight(String machineIconLight)
    {
        this.machineIconLight = machineIconLight;
    }
    
    @JsonIgnore
    public String getMachineIcon(String webColour)
    {
        return getComplimentaryOption(webColour, machineIconLight, machineIconDark);
    }

    public <T> T getComplimentaryOption(String webColour, T darkOption, T lightOption) {
        Color wc = Color.web(webColour);
        // brightness  =  sqrt( .241 R2 + .691 G2 + .068 B2 )
        // Don't know where these numbers came from!

        double brightness = Math.sqrt(0.241 * wc.getRed() * wc.getRed() + 0.691 * wc.getGreen() * wc.getGreen() + 0.068 * wc.getBlue() * wc.getBlue());
        if (brightness > 0.5)
            return darkOption;
        else
            return lightOption;
    }
}
