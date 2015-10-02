package morgoth.spike;


public class DataTypes {

    public static interface NeuronType {

        public boolean isInhibitory();
        public boolean isFixed();

        public float decay(); //a
        public float sensitivity(); //b
        public float vRreset(); //c
        public float uRreset(); //d

        public float threshold();

        public float vIinitial();
        public float uInitial();
        public float sInitial();

        public float ltpInitial();
        public float ltpDecay();

        public float ltdInitial();
        public float ltdDecay();

        public float iRreset();

        public float sMin();
        public float sMax();
        public float sdMin();
        public float sdMax();
    }

    public static interface NeuronInfo {

        public int id();
        public int typeId();
    }

    public static interface NeuronState {

        public float v();
        public float u();
        public float i();
    }

    public static interface SynapseInfo {

        public int id();
        public int srcId();
        public int dstId();
        public int delay();
    }

    public static interface SynapseState {

        public float s();
        public float sd();
    }
}
