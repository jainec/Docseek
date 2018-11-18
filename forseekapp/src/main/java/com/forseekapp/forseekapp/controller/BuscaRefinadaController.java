package com.forseekapp.forseekapp.controller;

import com.forseekapp.forseekapp.models.Busca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class BuscaRefinadaController {


    @RequestMapping(value = "/buscaRefinada", method = RequestMethod.GET)
    public String form() {
        return "busca/buscaRefinada";
    }

}
