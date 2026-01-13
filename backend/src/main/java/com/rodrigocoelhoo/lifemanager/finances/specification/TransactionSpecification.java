package com.rodrigocoelhoo.lifemanager.finances.specification;

import com.rodrigocoelhoo.lifemanager.finances.model.ExpenseCategory;
import com.rodrigocoelhoo.lifemanager.finances.model.TransactionModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<TransactionModel> withFilters(
            UserModel user,
            Long walletId,
            ExpenseCategory category,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user"), user));

            if (walletId != null) {
                predicates.add(
                        cb.and(
                                cb.equal(root.get("wallet").get("id"), walletId),
                                cb.equal(root.get("wallet").get("user"), user)
                        )
                );
            }


            if (category != null) {
                predicates.add(
                        cb.equal(root.get("category"), category)
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
