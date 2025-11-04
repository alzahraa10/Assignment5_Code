package amazon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AmazonUnitTest {

    interface PaymentGateway { boolean charge(String userId, double amount); }

    static class AmazonOrder {
        private final PaymentGateway gateway;
        AmazonOrder(PaymentGateway g){ this.gateway = g; }

        boolean place(String u, double t){
            if(t <= 0) return false;
            return gateway.charge(u,t);
        }
    }

    PaymentGateway gateway;
    AmazonOrder order;

    @BeforeEach
    void setup(){
        gateway = mock(PaymentGateway.class);
        order = new AmazonOrder(gateway);
    }

    @Test
    @DisplayName("specification-based")
    void validOrder(){
        when(gateway.charge("u1",10)).thenReturn(true);
        assertThat(order.place("u1",10)).isTrue();
    }

    @Test
    @DisplayName("structural-based")
    void rejectsZeroTotal(){
        assertThat(order.place("u1",0)).isFalse();
        verifyNoInteractions(gateway);
    }
}
