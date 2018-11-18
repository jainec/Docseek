package com.forseekapp.forseekapp.repository;

import edu.princeton.cs.algs4.*;
import static edu.princeton.cs.algs4.StdDraw.line;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
/**
 *
 * @author jaine e raul
 */
public class Docseek {
    //Estrutura de dados utilizada para salvar as palavras, onde a key
    //é uma String e o valor associado é um TreeSET com os títulos dos docs
    //em que a palavra (key) aparece
    TST<TreeSet> tst = new TST<>();

    Hashtable<String, String> tabelaHash = new Hashtable();

    //Abre o diretorio com os documentos.txt e salva o caminho de todos
    File folder = new File("C:\\Users\\raul1\\IdeaProjects\\forseekapp\\BoletimTXT");
    File[] listOfFiles = folder.listFiles();

    /*Função para ler um por um dos arquivos de listOfFiles e salvar
    todas as palavras desses arquivos na TST associando a elas o
    título dos documentos em que elas se encontram*/
    public void lerArquivos() throws FileNotFoundException, IOException {
        for (File file : listOfFiles) {
            //lê arquivo por arquivo
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"ISO-8859-1"));
            String line = null, title = null;
            int i = 0;
            //lê linha por linha do arquivo
            while((line = br.readLine()) != null) {
                i++;
                if(i == 1){   //se for a primeira linha então salvamos como título
                    title = line.replaceFirst("^\\s*", "");
                    tabelaHash.put(title, file.toString());
                }
                String[] words = line.split(" ");
                //lê palavra por palavra das linhas
                for (String word : words) {
                    if(!word.equals("") && word.length() > 1){   //retira . , : ; do final das palavras
                        word = word.replace(" ", "");
                        if (!Character.isAlphabetic(word.charAt(word.length()-1)) &&
                                !Character.isDigit(word.charAt(word.length()-1))){
                            word = word.substring(0, word.length() - 1);
                        }
                        word = word.toLowerCase();
                        TreeSet<String> titles = new TreeSet<String>();
                        /*se a palavra já existe na TST, atualiza o set de titulos
                        com o novo titulo encontrado*/
                        if(tst.get(word) != null) {
                            titles.addAll((Collection<? extends String>) tst.get(word));
                            titles.add(title);
                            tst.put(word, titles);
                        } else {   //se não existe, então adc pela primeira vez
                            titles.add(title);
                            tst.put(word, titles);
                        }
                    }
                }
            }
            br.close();
        }
    }

    /*Função que recebe uma lista de palavras inseridas pelo usuário e trata
    essas palavras retirando pontuações desnecessárias como (; , . :) do final
    delas, para assim poder busca-las na TST*/
    public ArrayList<String> trataEntrada(String[] lista) {
        ArrayList<String> lista_saida = new ArrayList<>();
        if(lista.length > tst.size()) {
            System.out.println("BUSCA MUITO LONGA!");
            return null;
        }
        for (int i = 0; i < lista.length; i++) {
            if(!lista[i].equals("") && lista[i].length() > 1){
                lista[i] = lista[i].replace(" ", "");
                if (!Character.isAlphabetic(lista[i].charAt(lista[i].length()-1)) &&
                        !Character.isDigit(lista[i].charAt(lista[i].length()-1))){
                    String last_char = lista[i].substring(lista[i].length() - 1);
                    if(!last_char.equals("*"))
                        lista[i] = lista[i].substring(0, lista[i].length() - 1);
                }
                if(lista[i].contains("("))
                    lista[i] = lista[i].replaceAll("\\(", "");
                if (lista[i].contains(")"))
                    lista[i] = lista[i].replaceAll("\\)", "");
                if(lista[i] != null)   //se no final do tratamento a palavra não for nula, adc na lista de retorno
                    lista_saida.add(lista[i]);
            }
        }
        return lista_saida;
    }

    /*Função que executa uma busca padrão, isto é, sem operador lógico OR, e
    considera espaços entre as palavras da query como um AND lógico*/
    public ArrayList<String> defaultSearch(String word) {
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> lista_titles = new ArrayList<>();
        word = word.toLowerCase();
        String words_array[] = word.split(" ");
        words = trataEntrada(words_array);   //chama a função para tratar a entrada
        ArrayList<String> lista = new ArrayList<>();
        for (String word1 : words) {  //varre palavra por palavra da entrada
            word1 = word1.toLowerCase();
            TreeSet<String> aux = new TreeSet<>();
            if(word1.contains("*")) {   //se for curinga
                word1 = word1.replaceAll("\\*", ".");
                TreeSet<String> arr = new TreeSet<>();
                ArrayList<String> arr2 = new ArrayList<>();
                for(String e: tst.keysThatMatch(word1.replace(" ", ""))){
                    arr = tst.get(e.replace(" ", ""));
                    arr2.addAll(arr);
                }
                aux = new TreeSet<>(arr2);
            } else
                aux = tst.get(word1.replace(" ", ""));
            if(aux == null || aux.size() < 1) {
                /*se não existe na TST e nem é curinga, então: busca não encontrada*/
                lista_titles.add("BUSCA NÃO ENCONTRADA");
                return lista_titles;
            }
            //se a palavra existe na TST, adiciona ela em "lista" e vai para a próxima palavra
            lista.addAll(aux);
        }
        /*Depois de buscar todas as palavras na TST e salvar os titulos dos docs em
        que elas aparecem, retira-se os titulos repetidos da lista de saida.
        Além disso, verifica o nº de ocorrência de cada título, para checar se
        respeitou o AND, isto é, as palavras buscadas existem simultaneamente em um
        determinado doc*/
        for (String title : lista) {
            int ocurrences = Collections.frequency(lista, title);
            if (ocurrences == words.size() && !lista_titles.contains(title))
                lista_titles.add(title);
        }
        return lista_titles;
    }

    /*Função que recebe uma frase e busca o documento em que ela ocorre respeitando
    os operadores lógicos*/
    public ArrayList<String> buscar(String word) {
        ArrayList<String> lista_titles = new ArrayList<>();
        ArrayList<String> lista_titles2 = new ArrayList<>();
        /*Se a busca não possuir o operador lógico OR, basta chamar a função
        defaultSearch explicada acima e salvar seu resultado em lista_titles*/
        if (!word.contains(" OR ")) {
            lista_titles = defaultSearch(word);
        } else {    //Se contém _OR_
            /*Se contém OR mas não contém () parenteses, então segue a lógica:
            processa da esquerda para a direita (como o Google faz)*/
            if(!word.contains("(")) {
                String words[] = word.split(" OR ");
                for (String word1 : words)
                    lista_titles.addAll(defaultSearch(word1));
                TreeSet<String> set = new TreeSet<>(lista_titles);
                lista_titles = new ArrayList<>(set);
            } else {    //Se contém OR e () parênteses
                String[] words = word.split("\\)");
                ArrayList<String[]> combinations = new ArrayList<>();
                /*Pega cada expressão dentro de cada parênteses e salva em um array
                de String[] e então cada array desse é colocado em um ArrayList.
                Ex: entrada -> (A OR B) (C OR D)
                ArrayList: [[A, B], [C, D]*/
                for (String word1 : words) {
                    if(word1.contains("(")) word1 = word1.replaceAll("\\(", "");
                    word1 = word1.replace(" ", "");
                    if(word1.contains("OR")) {
                        String[] aux = word1.split("OR");
                        combinations.add(aux);
                    } else{
                        String[] aux = new String[1];
                        aux[0] = word1;
                        combinations.add(aux);
                    }
                }
                //Depois de gerar o arrayList, busca todas as combinações possíveis
                lista_titles2 = buscaCombinacoes(combinations, lista_titles2);

                if(lista_titles2.contains("BUSCA NÃO ENCONTRADA")) {
                    lista_titles.add("BUSCA NÃO ENCONTRADA");
                } else {  //Retira os titulos repetidos e verifica os que seguiram as retrições de AND
                    for (String title : lista_titles2) {
                        int ocurrences = Collections.frequency(lista_titles2, title);
                        if (ocurrences == words.length && !lista_titles.contains(title))
                            lista_titles.add(title);
                    }
                }
            }
        }
        return lista_titles;
    }


    /*Função que recebe um ArrayList com as combinações de OR e retorna um
    ArraList com o resultado das buscas dessas combinações*/
    public ArrayList<String> buscaCombinacoes (ArrayList<String[]> combinations, ArrayList<String> lista_titles2){
        if(combinations.size() > tst.size()) {
            System.out.println("BUSCA MUITO LONGA!");
            return null;
        }
        for (String[] combination : combinations) {
            int k = 0;
            for (String string : combination) {
                ArrayList<String> res = new ArrayList<>();
                res = defaultSearch(string);   //busca palavra por palavra na TST
                if(k == 0)
                    lista_titles2.addAll(res);   //se for a 1ª palavra da expressão OR, adc
                else { //Se não for a 1º palavra, só adc o título se ele já não tiver sido adicionado
                    for (String re : res) {
                        if(!lista_titles2.contains(re))
                            lista_titles2.add(re);
                    }
                }
                k++;
            }
        }
        return lista_titles2;
    }


    /*Função que recebe uma lista de títulos e retorna uma lista com o caminho
    do .txt para cada titulo*/
    public ArrayList<String> getPaths(ArrayList<String> titles) {
        ArrayList<String> paths = new ArrayList<>();
        for (String title : titles) {
            String pt = tabelaHash.get(title);
            paths.add(pt);
        }
        return paths;
    }

    /*
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Docseek t = new Docseek();
        t.lerArquivos();
        Scanner scanf = new Scanner(System.in);
        String query = "a";
        while(!query.equals("stop")) {
            System.out.println("Insira a busca que deseja fazer: ");
            query = scanf.nextLine();
            ArrayList<String> res = new ArrayList<>();
            res = t.buscar(query);
            System.out.println(res);
            System.out.println(t.getPaths(res));
        }
    }*/
}


