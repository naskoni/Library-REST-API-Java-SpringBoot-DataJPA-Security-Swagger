package com.naskoni.library.specification;

import com.naskoni.library.entity.AbstractEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SearchSpecification<T extends AbstractEntity> implements Specification<T> {

  private static final long serialVersionUID = -7349634658746608071L;

  private final transient SearchCriteria criteria;

  public SearchSpecification(SearchCriteria searchCriteria) {
    this.criteria = searchCriteria;
  }

  @Override
  public Predicate toPredicate(
      Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (criteria.getOperation().equals(">")) {
      return criteriaBuilder.greaterThanOrEqualTo(
          root.get(criteria.getKey()), criteria.getValue().toString());
    } else if (criteria.getOperation().equals("<")) {
      return criteriaBuilder.lessThanOrEqualTo(
          root.get(criteria.getKey()), criteria.getValue().toString());
    } else if (criteria.getOperation().equals(":")) {
      if (root.get(criteria.getKey()).getJavaType() == String.class) {
        return criteriaBuilder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
      } else {
        return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
      }
    }

    return null;
  }
}
