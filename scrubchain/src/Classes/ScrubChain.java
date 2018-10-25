package Classes;

import Helpers.StringHelper;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;

public class ScrubChain {
    public static ArrayList<Block> scrubChain = new ArrayList<>();
    public static int difficulty = 6;

    public static void main(String[] args){
        //Making a chain
        addBlock(new Block("OG block", "0"));
        addBlock(new Block("Firstborn", scrubChain.get(scrubChain.size()-1).getHash()));
        addBlock(new Block("Second in line", scrubChain.get(scrubChain.size()-1).getHash()));
        //JSON output
        String scrubChainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(scrubChain);
        System.out.println(scrubChainJSON);
        System.out.println(isChainValid());
    }

    public static boolean isChainValid(){
        Block curr;
        Block prev;
        String target = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < scrubChain.size(); i++) {
            curr = scrubChain.get(i);
            prev = scrubChain.get(i-1);

            //Check that curr hash is true, that prev hash is true and that block is mined
            if(!curr.getHash().equals(curr.createHash()) ||
               !prev.getHash().equals(curr.getPrevHash()) ||
               !curr.getHash().substring(0, difficulty).equals(target)){
                return false;
            }
        }
        return true;
    }

    //add function to encapsulate mining
    public static void addBlock(Block newBlock){
        int mine_index = scrubChain.size();
        scrubChain.add(newBlock);
        System.out.println("Starting to mine block " + mine_index + " with difficulty: " + difficulty);
        scrubChain.get(mine_index).mineBlock(difficulty);

    }
}
