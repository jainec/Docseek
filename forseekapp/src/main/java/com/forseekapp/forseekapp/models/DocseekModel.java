package com.forseekapp.forseekapp.models;

import com.forseekapp.forseekapp.repository.Docseek;

import java.io.IOException;
import java.util.ArrayList;

public class DocseekModel {

    private Docseek doc = new Docseek();

    public DocseekModel(){
        try {
            doc.lerArquivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList <String> search(String query) {
        return doc.buscar(query);
    }

    public ArrayList <String> path(ArrayList<String> res) {
        return doc.getPaths(res);
    }


}
