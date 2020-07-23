package morgoth.spike;

public abstract class Cell {

    public static class Info {
        /*

  size_t numNeuronTypes, numNeurons, numDynamicNeurons, numSynapses;
  size_t maxSynapsesPerNeuron, maxSynapsesPerNeuronPow;

#ifdef FATSYNSIN
  typedef uint64 SynapseInEntry;
#else
  typedef SynapseIdx SynapseInEntry; // *NOT* really synapseIdx but srcIdx * maxSynsByD + delay
#endif

  Buf<NeuronTypeInfo> neuronTypes;

  Buf<NeuronId> neuronIds;
  Buf<NeuronTypeIdx> neuronTypeIdxs;

  BinMap<NeuronId, NeuronIdx> neuronIdIdxs;
  BinMap<NeuronId, NeuronIdx> importedNeuronIdIdxs;
  BinMap<CellId, DelayTime> importedCellIdMinDelays;

  Buf<NeuronIdx> neuronTypeBases;
  Buf<NeuronIdx> neuronTypeNumLocal; //not actually an idx, a count

  Buf<SynapseIdx> synapseOutBases; //by delay - has to be for SynapsesOutIt - need base by d

  NumListBlock<NeuronIdx> synapsesOut; //by delay
  NumListBlock<SynapseInEntry> synapsesIn; //by delay
       */
    }

    protected Info info;

    public static class State {
    }

    protected State state;

    public abstract void initState();

    public abstract void setState();

    public abstract void next(long simTime);

    public abstract void fire();

    public abstract void fire(int idx);

    public abstract void propegate();

    public abstract void step();

    public abstract void update();

    public abstract void inject(int idx, float dI);

    // const RingBuf<PagedList<NeuronIdx>, MaxDelay> getFires() const;
}
