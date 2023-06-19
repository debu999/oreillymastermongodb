package org.doogle.resource;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.doogle.entity.PaymentEntity;
import org.doogle.mappers.PaymentMapper;
import org.doogle.model.Payment;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@ApplicationScoped
public class PaymentResource {

    @Inject
    PaymentMapper paymentMapper;

    @Query("Payment")
    public Uni<List<Payment>> getAllPayments() {

        Uni<List<PaymentEntity>> paymentEntityUni = PaymentEntity.listAll();
        return paymentEntityUni.log().map(u -> paymentMapper.fromPaymentEntities(u)).log();
    }

    @Mutation("addUpdatePayment")
    public Uni<Payment> addPayment(@Name("payment") Payment payment) {

        PaymentEntity u = paymentMapper.toPaymentEntity(payment);
        Log.info(u);
        return Uni.createFrom().item(u).flatMap(PaymentEntity::persistOrUpdatePayment).log("Response").map(obj -> paymentMapper.fromPaymentEntity(obj)).log();
    }
}
