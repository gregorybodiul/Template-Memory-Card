package com.bgexample.memorycards;

import java.util.ArrayList;

public class Cards {
    private ArrayList<Integer> cards = new ArrayList<>();

    public Cards(){
        cards.add(R.drawable.item0);
        cards.add(R.drawable.item1);
        cards.add(R.drawable.item2);
        cards.add(R.drawable.item3);
        cards.add(R.drawable.item4);
        cards.add(R.drawable.item5);
        cards.add(R.drawable.item6);
        cards.add(R.drawable.item7);
        cards.add(R.drawable.item8);
        cards.add(R.drawable.item9);
        cards.add(R.drawable.item10);
        cards.add(R.drawable.item11);
        cards.add(R.drawable.item12);
        cards.add(R.drawable.item13);
        cards.add(R.drawable.item14);
        cards.add(R.drawable.item16);
        cards.add(R.drawable.item17);
    }

    public Integer getSrc(int index){
        return cards.get(index);
    }

    public int getSize(){
        return cards.size();
    }
}
