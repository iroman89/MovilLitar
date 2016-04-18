package cl.softmedia.movillitar.utils;


public class BasicMapper {

    public static void Map(Object oSource, Object oDestination) {
        Class<? extends Object> oClassSource = oSource.getClass();
        Class<? extends Object> oClassDestination = oDestination.getClass();

        if (oClassSource.getSimpleName().equals("PedidoDTO")
                && oClassDestination.getSimpleName().equals("Pedido")) {

//            PedidoDTO oPedidoDTO = (PedidoDTO) oSource;
//            Pedido oPedido = (Pedido) oDestination;
//
//            oPedido.dtFechaPedido = oPedidoDTO.sFechaCreacion;
//            oPedido.iCantidadArriendo = oPedidoDTO.iCantidadArriendo;
//            oPedido.iCantidadCargas = oPedidoDTO.iCantidadCargas;
        }
    }
}
