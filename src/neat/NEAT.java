package neat;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class Node{
  float totalInput;
  String activationType;
  public Node(String activationType){
    this.activationType=activationType;
  }
  public Node(){
    this.activationType = "Sigmoid";
  }
  public void add(float input){
    this.totalInput += input;
  }
  public float activate(){
    float output;
    switch (this.activationType){
        case "ReLU":
            output = (this.totalInput <= 0) ? 0 : this.totalInput;
            break;
        case "Sigmoid":
            output =  1f/(1f + (float) Math.exp(this.totalInput * -1f));
            break;
        case "Linear":
            output = this.totalInput;
            break;
        default:
            output = this.totalInput;
            break;
    }
    this.totalInput = 0;
    return output;
  }
}
class GenotypeList{
    List<Genotype> genotypes = new ArrayList<>();
    public void addGenotype(Genotype newGenotype){
        genotypes.add(newGenotype);
        this.sortGenotypes();
    }
    public List<Integer> getInnovation(){
        List<Integer> outputInnovation = new ArrayList<>();
        for (int i=0;i<genotypes.size();i++){
            outputInnovation.add(genotypes.get(i).innovationNumber);
        }
        Collections.sort(outputInnovation);
        return outputInnovation;
    }
    public int isConnectionGenotype(Connection matchConnect){
        int innovationNumber=0;
        
        return innovationNumber;
    }
    public void sortGenotypes(){
        List<Genotype> currentGenotypeList = this.genotypes;
        List<Genotype> newGenotypeList = new ArrayList<>();
        List<Integer> fromNode = new ArrayList<>();
        for(int i=0;i<this.genotypes.size()-1;i++){
            boolean isSorted = true;
            Genotype higherNode=this.genotypes.get(0);
            for (int j=1; j<this.genotypes.size()-(i+1);j++){
                String higherPos = higherNode.connectionVector.fromPos;
                String fromPos = this.genotypes.get(i).connectionVector.fromPos;
                boolean higherStillTop = true;
                if(higherPos.contains("i") && fromPos.contains("h")){
                    higherStillTop = false;
                }else if(higherPos.contains("i") && fromPos.contains("i")){
                    int higherPlace = Integer.parseInt(higherPos.replace("i",""));
                    int fromPlace = Integer.parseInt(fromPos.replace("i",""));
                    if(fromPlace>higherPlace){
                        higherStillTop = false;
                    }
                }else if(higherPos.contains("h") && fromPos.contains("h")){
                    String[] higherSplit = higherPos.split("h");
                    String[] fromSplit = fromPos.split("h");
                    int higherRow = Integer.parseInt(higherSplit[0]);
                    int fromRow = Integer.parseInt(fromSplit[0]);
                    if(fromRow>higherRow){
                        higherStillTop = false;
                    }else if(fromRow==higherRow){
                        int higherPlacement = Integer.parseInt(higherSplit[1]);
                        int fromPlacement = Integer.parseInt(fromSplit[1]);
                        if(fromPlacement>higherPlacement){
                            higherStillTop = false;
                        }
                    }
                }
                if (higherStillTop){
                    newGenotypeList.add(this.genotypes.get(i));
                    isSorted = false;
                }else{
                    newGenotypeList.add(higherNode);
                    higherNode = this.genotypes.get(i);
                }
            }
            currentGenotypeList = newGenotypeList;
            newGenotypeList = new ArrayList<>();
            if (isSorted){
                break;
            }
        }
        this.genotypes = currentGenotypeList;
    }
    public List<Connection> getConnectionsFor(String node){
        List<Connection> connections = new ArrayList<>();
        for (int i=0;i<this.genotypes.size();i++){
            if (this.genotypes.get(i).connectionVector.fromPos == node){
                connections.add(this.genotypes.get(i).connectionVector);
            }
        }
        return connections;
    }
}
class Genotype{
    public void Genotype(Connection connection, int innovation, boolean disabled){
        this.connectionVector = connection;
        this.innovationNumber = innovation;
        this.disabled = disabled;
    }
    public void Genotype(Connection connection, int innovation){
        this.connectionVector = connection;
        this.innovationNumber = innovation;
        this.disabled = false;
    }
    Connection connectionVector;
    int innovationNumber;
    public boolean disabled;
}

class Connection{
    public Connection(String fromPos, String toPos){
        this.fromPos = fromPos;
        this.toPos = toPos;
    }
    public String fromPos;
    public String toPos;
}
class Specimen{
    float bias = 1f;
    int inputSize;
    int outputSize;
    List<List<Node>> nodeLayers = new ArrayList<>();
    GenotypeList genomes;
    public Specimen(int inputSize, int outputSize){
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        List<Node> inputLayer = new ArrayList<>();
        for (int i=0; i<inputSize;i++){
            inputLayer.add(new Node("Linear"));
        }
        this.nodeLayers.add(inputLayer);
        // Hidden Layere (Between 0 and 2)
        for (int row=0;row<(int)(Math.random()*2);row++){
            List<Node> hiddenLayer = new ArrayList<>();
            for (int i=1;i<1+(int)(Math.random()*4);i++){
                hiddenLayer.add(new Node());
            }
            this.nodeLayers.add(hiddenLayer);
        }
        List<Node> outputLayer= new ArrayList<>();
        for (int node=0;node<outputSize;node++){
            inputLayer.add(new Node("Linear"));
        }
        this.nodeLayers.add(outputLayer);
    }
    public int getTotalNodeCount(){
        int ConnectionCount = 0;
        for(int layer=1;layer<this.nodeLayers.size();layer++){
            ConnectionCount += this.nodeLayers.get(layer).size();
        }
        return ConnectionCount;
    }
    public void addRandomConnection(){
        int fromConnectionCount = -1;
        for (int layer=0;layer<this.nodeLayers.size()-1;layer++){
            fromConnectionCount+=this.nodeLayers.get(layer).size();
        }
        int fromNode = (int)(Math.random()*fromConnectionCount);
        int toConnectionCount = this.getTotalNodeCount();
        int toNode = (int)(fromNode+1+(int)(Math.random()*(toConnectionCount-fromNode)));
        //Encode from position
        String fromPosition = "";
        if (fromNode<this.nodeLayers.get(0).size()){
            fromPosition = "i"+Integer.toString(fromNode);
        }else{
            fromNode-=this.nodeLayers.get(0).size()-1;
            for(int hiddenLayer=1;hiddenLayer<this.nodeLayers.size()-1;hiddenLayer++){
                if (fromNode<this.nodeLayers.get(hiddenLayer).size()){
                    fromPosition = Integer.toString(hiddenLayer-1)+"h"+Integer.toString(fromNode);
                }else{
                    fromNode-=this.nodeLayers.get(hiddenLayer).size()-1;
                }
            } 
        }
        //Encode to position
        String toPosition = "";
        if (toNode>fromConnectionCount){
            toPosition = "o"+Integer.toString(toNode-fromConnectionCount);
        }else{
            for(int hiddenLayer=1;hiddenLayer<this.nodeLayers.size()-1;hiddenLayer++){
                if (toNode<this.nodeLayers.get(hiddenLayer).size()){
                    toPosition = Integer.toString(hiddenLayer-1)+"h"+Integer.toString(fromNode);
                }else{
                    toNode-=this.nodeLayers.get(hiddenLayer).size()-1;
                }
            } 
        }
        Connection connection = new Connection(fromPosition,toPosition);
    }
    public String getShape(){
        String shape = "";
        for (int i=0;i<this.nodeLayers.size();i++){
            shape+=Integer.toString(this.nodeLayers.get(i).size());
        }
        return shape;
    }
    public List<Float> activate(List<Float> input){
        if(input.size() != this.nodeLayers.get(0).size()){
            throw new java.lang.Error("Mismatched input and inputNodes count");
        }
        for (int i=0;i<this.nodeLayers.get(0).size();i++){
            this.nodeLayers.get(0).get(i).add(input.get(i));
            float nodeOutput = this.nodeLayers.get(0).get(i).activate();
            List<Connection> connections = this.genomes.getConnectionsFor("i"+Integer.toString(i));
            for (int connection=0;connection<connections.size();connection++){
                String toPos = connections.get(connection).toPos;
                if (toPos.contains("o")){
                    this.nodeLayers.get(this.nodeLayers.size()-1).get(Integer.parseInt(toPos.replace(toPos, "o"))).add(nodeOutput);
                }else if(toPos.contains("h")){
                    String[] hiddenPlace = toPos.split("h");
                    this.nodeLayers.get(Integer.parseInt(hiddenPlace[0])+1).get(Integer.parseInt(hiddenPlace[1])).add(nodeOutput);
                }
            }
            
        }
        
        List<Float> output = new ArrayList<>();
        return output;
    }
    @Override
    public Object clone()throws CloneNotSupportedException{
        try{
            return super.clone();
        }catch (CloneNotSupportedException e){
            return new Specimen(this.inputSize, this.outputSize);
        }
    }    
}

class Species{
    List<Specimen> population = new ArrayList<>();
    String speciesShape = "";
    public Species(Specimen speciesBase){
        population.add(speciesBase);
        speciesShape = speciesBase.getShape();
    }
}

class GenePool{
    List<Species> population = new ArrayList<>();
    GenotypeList allGenotypes = new GenotypeList();
}

public class NEAT{
    public static void main(String[] args){
        Node test = new Node("ReLU");
        test.add(1.05f);
        test.add(.25f);
        System.out.println(test.activate());
        test.add(-2.5f);
        System.out.println(test.activate());
  }
}