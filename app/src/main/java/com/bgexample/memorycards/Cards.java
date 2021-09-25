package com.bgexample.memorycards;

import java.util.ArrayList;

public class Cards {
    private ArrayList<Integer> cards = new ArrayList<>();

    public Cards(){
        cards.add(R.drawable.img1);
        cards.add(R.drawable.img2);
        cards.add(R.drawable.img3);
        cards.add(R.drawable.img4);
        cards.add(R.drawable.img5);
        cards.add(R.drawable.img6);
        cards.add(R.drawable.img7);
        cards.add(R.drawable.img8);
        cards.add(R.drawable.img9);
        cards.add(R.drawable.img10);
        cards.add(R.drawable.img11);
        cards.add(R.drawable.img12);
        cards.add(R.drawable.img13);
        cards.add(R.drawable.img14);
        cards.add(R.drawable.img15);
        cards.add(R.drawable.img16);
        cards.add(R.drawable.img17);
        cards.add(R.drawable.img18);
    }

    public Integer getSrc(int index){
        return cards.get(index);
    }

    public int getSize(){
        return cards.size();
    }
}
