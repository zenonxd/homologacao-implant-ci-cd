package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.*;
import com.devsuperior.dscommerce.entities.*;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    //como o service devolve um DTO para o controller,
    //a função retornará um DTO
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {

        //busca no banco de dados, caso não ache, lançará uma exceção
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado."));

        //pegando o cliente associado ao pedido
        //se a condição do método passar, ele vai retornar o DTO ali embaixo
        //se não, dará a exception Forbidden
        authService.validateSelfOrAdmin(order.getClient().getId());

        //lembrar que no DTO foi criado um construtor
        //específico para receber um Product
        return new OrderDto(order);
    }

    @Transactional
    public OrderDto insert(OrderDto dto) {
        Order order = new Order();

        //instant.now porque é o momento que o pedido está sendo salvo
        order.setMoment(Instant.now());
        //como acabou de inserir o pedido, ele está aguardando pagamento ainda
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        //para settar o client, importamos o UserService e utilizamos o authenticate
        //para usar o cliente que está logado
        User user = userService.authenticate();
        order.setClient(user);

        //isso aqui a gente varre os itens que vieream na requisição do Postman
        //acessamos a lista do OrderDto que é OrderItemDto
        for (OrderItemDto itemDto : dto.getItems()) {
            //sabemos que o OrderItem tem relação com Product e Order, lembra do UML?
            //assim sendo, instanciaremos o Product e Order para fazer a relação entre eles.

            //Instanciando o product a partir da referência do item passado no Postman
            //(foi importado o productRepository lá em cima)
            Product product = productRepository.getReferenceById(itemDto.getProductId());

            //Instanciando um OrderItem e usando o seu construtor, passando o order ali de cima, o Product
            // e a quantidade e preço retirada do itemDto.
            //Assim, associamos o item com Order e Product.
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());

            //associando por fim, o OrderItem ao Order lá de cima.
            order.getItems().add(item);
        }
        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());
        return new OrderDto(order);
    }


}
