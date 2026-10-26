export interface AddToCartRequest {
  isbn: string;
  quantity: number;
}

export interface CartItemResponse {
  isbn: string;
  title: string;
  author: string;
  unitPrice: number;
  quantity: number;
  totalPrice: number;
}

export interface CartResponse {
  userEmail: string;
  items: CartItemResponse[];
  cartTotal: number;
}
