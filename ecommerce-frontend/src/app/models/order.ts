export interface OrderItemResponse {
  isbn: string;
  title: string;
  author: string;
  quantity: number;
  purchasedPrice: number;
  totalPrice: number;
}

export interface OrderResponse {
  orderNumber: string;
  userEmail: string;
  orderDate: string; // Le date in JSON viaggiano come stringhe ISO
  totalAmount: number;
  status: string;
  items: OrderItemResponse[];
}
