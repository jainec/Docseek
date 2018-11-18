package com.forseekapp.forseekapp.controller;

import com.forseekapp.forseekapp.models.Arquivo;
import com.forseekapp.forseekapp.models.Busca;
import com.forseekapp.forseekapp.models.DocseekModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
public class IndexController {

    ArrayList <String> arr = new ArrayList<>();
    ArrayList <String> pat = new ArrayList<>();
    ArrayList <Arquivo> arqs = new ArrayList<>();
    DocseekModel doc = new DocseekModel();
    String query;

    private String replaceBar(String path) {
        if(path == null) return "CAMINHO NAO ENCONTRADO";
        return path.replace('\\','/');

    }

    private void mapeamento(String nome){
        System.out.println(nome);
        arr = doc.search(nome);
        pat = doc.path(arr);
        arqs = new ArrayList<>();
        System.out.println(pat);
        for (int i = 0; i < arr.size(); i++) {
            String titulo = arr.get(i);
            String caminho = pat.get(i);
            caminho = "file:///" + replaceBar(caminho);
            Arquivo arq = new Arquivo(titulo, caminho);
            arqs.add(arq);
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public String form(Busca busca,RedirectAttributes attributes) {
        if(busca.getNome() == null){
            attributes.addFlashAttribute("mensagem", "Verifique os campos!");
            return "redirect:/";
        }

        this.query = busca.getNome();
        mapeamento(busca.getNome());

        attributes.addFlashAttribute("mensagem", "Busca efetuada com sucesso!");
        return  "redirect:/buscas";

    }

    @RequestMapping(value="/buscas", method = RequestMethod.POST)
    public String form2(Busca busca,RedirectAttributes attributes) {
        if(busca.getNome() == null){
            attributes.addFlashAttribute("mensagem", "Verifique os campos!");
            return "redirect:/";
        }

        query = busca.getNome();
        mapeamento(busca.getNome());

        attributes.addFlashAttribute("mensagem", "Busca efetuada com sucesso!");
        return  "redirect:/buscas";

    }

    @RequestMapping("/buscas")
    public ModelAndView listaBuscas(){

            ModelAndView mv = new ModelAndView("busca/listarBuscas");
        mv.addObject("query", query);
        mv.addObject("buscas", arqs);
        return mv;
    }

}
