package com.rodrigocoelhoo.lifemanager.finances.specification;

import com.rodrigocoelhoo.lifemanager.finances.model.TransferenceModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransferenceSpecification {

    public static Specification<TransferenceModel> withFilters(
            UserModel user,
            Long senderId,
            Long receiverId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user"), user));

            if (senderId != null) {
                predicates.add(
                        cb.and(
                                cb.equal(root.get("fromWallet").get("id"), senderId),
                                cb.equal(root.get("fromWallet").get("user"), user)
                        )
                );
            }

            if (receiverId != null) {
                predicates.add(
                        cb.and(
                                cb.equal(root.get("toWallet").get("id"), receiverId),
                                cb.equal(root.get("toWallet").get("user"), user)
                        )
                );
            }

            if (startDate != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("date"), startDate)
                );
            }

            if (endDate != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("date"), endDate)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
