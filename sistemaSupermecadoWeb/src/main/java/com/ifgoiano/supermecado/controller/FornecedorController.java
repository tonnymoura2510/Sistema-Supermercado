package com.ifgoiano.supermecado.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ifgoiano.supermecado.model.Fornecedor;
import com.ifgoiano.supermecado.repository.Fornecedores;
import com.ifgoiano.supermecado.service.CadastroFornecedorService;
import com.ifgoiano.supermecado.service.exception.FornecedorNomeJaCadastradoException;
import com.ifgoiano.supermecado.service.filtro.FornecedorFiltro;





@Controller
@RequestMapping("/fornecedores")
public class FornecedorController {
	
	private static final String  CADASTRO_VIEW = "fornecedor/CadastroFornecedor";
	
	@Autowired
	private Fornecedores forne;
	

	
	@Autowired
	private CadastroFornecedorService cadastroFornecedorService;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Fornecedor fo){
		
		ModelAndView mv = new ModelAndView("fornecedor/CadastroFornecedor");	
		mv.addObject("fornecedor", fo);			
		return mv;
	}
	@RequestMapping(value="/fornecedores/{codigo}", method = RequestMethod.POST)
	public String atualizar(@PathVariable Long codigo,Fornecedor fornecedor, RedirectAttributes attributes){		
		forne.save(fornecedor);
		attributes.addFlashAttribute("mensagem", "Fornecedor Atualizado com sucesso!");		
		return "redirect:/fornecedores";
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@Validated Fornecedor fornecedor, Errors erros, RedirectAttributes attributes){
		
		if(erros.hasErrors()){
			return "fornecedor/CadastroFornecedor";
		}
		
		try{
			if(fornecedor.getIdFornecedor() !=0){
				forne.save(fornecedor);
				attributes.addFlashAttribute("mensagem", "Fornecedor Atualizado com sucesso!");
				return "redirect:/fornecedores/novo";
			}else
			{
				cadastroFornecedorService.salvar(fornecedor);
				attributes.addFlashAttribute("mensagem", "Título salvo com sucesso!");
				//return "redirect:/fornecedores/novo";
			}
			return "redirect:/fornecedores/novo";
			
		}catch(FornecedorNomeJaCadastradoException e ){
			erros.rejectValue("nome", e.getMessage(),e.getMessage());
			return "fornecedor/CadastroFornecedor";
		}
		
		
		
		
		
	}
	
	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Fornecedor> pesquisar(String nome) {
		validarTamanhoNome(nome);
		return forne.findByNomeContainingIgnoreCase(nome);
	}
	
	private void validarTamanhoNome(String nome) {
		if (StringUtils.isEmpty(nome) || nome.length() < 3) {
			throw new IllegalArgumentException();
		}
	}
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") FornecedorFiltro filtro){
		
		List<Fornecedor> todosFornecedores = cadastroFornecedorService.filtrar(filtro);	
		
		ModelAndView mv = new ModelAndView("fornecedor/PesquisaFornecedores");
		mv.addObject("fornecedores", todosFornecedores);
		return mv;
	}
	
	
	@RequestMapping("{codigo}")//Aqui estamos recebemos o valor da variavel que vem da view
	public ModelAndView edicao(@PathVariable Long codigo ){//declaramos o @pathvariable + mais uma variavel para que possamos receber o valor
														// e trabalhamos com ela
	   //estamos recuperando o codigo do bando de dados
	    Fornecedor fornecedor = forne.findOne(codigo);
		ModelAndView mv = new ModelAndView("fornecedor/CadastroFornecedor");
		mv.addObject(fornecedor);//passamos o que recuperamos para a view
		return mv;
		
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
		forne.delete(id);

		attributes.addFlashAttribute("mensagem", "Título excluído com sucesso!");
		return "redirect:/fornecedores";

	}
	
	
	
	

}



