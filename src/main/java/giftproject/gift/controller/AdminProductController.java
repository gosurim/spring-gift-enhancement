package giftproject.gift.controller;

import giftproject.gift.dto.ProductRequestDto;
import giftproject.gift.dto.ProductResponseDto;
import giftproject.gift.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showProductList(Model model) {
        List<ProductResponseDto> products = productService.findAll();
        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductRequestDto("", 0, ""));
        model.addAttribute("isEdit", false);
        return "product/form";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute("product") ProductRequestDto requestDto,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "product/form";
        }
        productService.save(requestDto);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductResponseDto product = productService.findById(id);
        model.addAttribute("product",
                new ProductRequestDto(product.name(), product.price(), product.imageUrl()));
        model.addAttribute("productId", product.id());
        model.addAttribute("isEdit", true);
        return "product/form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
            @Valid @ModelAttribute("product") ProductRequestDto requestDto,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("productId", id);
            return "product/form";
        }
        productService.update(id, requestDto.name(), requestDto.price(),
                requestDto.imageUrl());
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
}
