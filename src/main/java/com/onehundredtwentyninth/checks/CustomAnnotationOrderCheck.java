package com.onehundredtwentyninth.checks;

import com.google.common.collect.Comparators;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

import java.util.*;

public class CustomAnnotationOrderCheck extends AbstractCheck {

    private final Set<String> customOrderRules = new LinkedHashSet<>();

    public int[] getDefaultTokens() {
        return new int[]{
                TokenTypes.CLASS_DEF,
                TokenTypes.INTERFACE_DEF,
                TokenTypes.PACKAGE_DEF,
                TokenTypes.ENUM_CONSTANT_DEF,
                TokenTypes.ENUM_DEF,
                TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF,
                TokenTypes.VARIABLE_DEF,
                TokenTypes.RECORD_DEF,
                TokenTypes.COMPACT_CTOR_DEF,
        };
    }

    public int[] getAcceptableTokens() {
        return new int[]{
                TokenTypes.CLASS_DEF,
                TokenTypes.INTERFACE_DEF,
                TokenTypes.PACKAGE_DEF,
                TokenTypes.ENUM_CONSTANT_DEF,
                TokenTypes.ENUM_DEF,
                TokenTypes.METHOD_DEF,
                TokenTypes.CTOR_DEF,
                TokenTypes.VARIABLE_DEF,
                TokenTypes.ANNOTATION_DEF,
                TokenTypes.ANNOTATION_FIELD_DEF,
                TokenTypes.RECORD_DEF,
                TokenTypes.COMPACT_CTOR_DEF,
        };
    }

    public int[] getRequiredTokens() {
        return CommonUtil.EMPTY_INT_ARRAY;
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF || ast.getType() == TokenTypes.METHOD_DEF) {
            var isNeededAnnotationsPresented = AnnotationUtil.containsAnnotation(ast, customOrderRules);
            if (isNeededAnnotationsPresented) {
                LinkedList<Integer> order = new LinkedList<>();
                customOrderRules.forEach(s -> {
                    var annotation = Optional.ofNullable(AnnotationUtil.getAnnotation(ast, s));
                    annotation.ifPresent(a -> order.add(a.getLineNo()));
                });

                if (!Comparators.isInOrder(order, Integer::compareTo)) {
                    log(order.get(0), "Wrong annotations order. Expected order: " + customOrderRules);
                }
            }
        }
    }

    public final void setCustomAnnotationOrderRules(final String inputCustomAnnotationOrder) {
        customOrderRules.addAll(Arrays.asList(
                inputCustomAnnotationOrder.trim().replaceAll(" +", " ").split(" "))
        );
    }
}
