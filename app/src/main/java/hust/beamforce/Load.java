package hust.beamforce;

public class Load {
    private Beam beam;
    private String type;
    private Double value;
    private Double left_position,right_position;

    public Load(Beam beam) {
        this.beam = beam;
    }

    public boolean setType(String type) {
        this.type = type;
        return true;
    }

    public String getType() {
        return this.type;
    }

    public boolean setValue(Double value) {
        if(value != 0) {
            this.value = value;
            return true;
        } else {
            return false;
        }
    }

    public Double getValue() {
        return this.value;
    }

    public boolean setPosition(Double left_position,Double right_position) {
        if(left_position >= 0 && right_position >= 0
                && left_position <= right_position
                && left_position <= beam.getLength()
                && right_position <= beam.getLength())
        {
            this.left_position = left_position;
            this.right_position = right_position;
            return true;
        } else {
            return false;
        }
    }

    public Double getLeftPosition() {
        return this.left_position;
    }

    public Double getRightPosition() {
        return this.right_position;
    }

    public boolean equals(Load load){
        if(this == load) {
            return true;
        }
        if(load != null && load.getClass() == Load.class) {
            if(this.getType().equals(load.getType())
                    && this.getValue().equals(load.getValue())
                    && this.getLeftPosition().equals(load.getLeftPosition())
                    && this.getRightPosition().equals(load.getRightPosition())){
                return true;
            }
        }
        return false;
    }

    public String info() {
        String unity="";
        switch (getType()){
            case "均布力":{
                unity += "N/m";
                break;
            }
            case "集中力":{
                unity += "N";
                break;
            }
            case "弯矩":{
                unity += "N•m";
                break;
            }
        }
        return "载荷类型："+ type + " 载荷大小:" + value +unity+ "\n作用点：" + left_position + "m:" + right_position + "m\n";
    }
}
