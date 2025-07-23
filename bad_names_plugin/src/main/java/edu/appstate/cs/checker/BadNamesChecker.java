package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.*;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(
        name = "BadNamesChecker",
        summary = "Poor-quality identifiers",
        severity = WARNING,
        linkType = CUSTOM,
        link = "https://github.com/plse-Lab/"
)
public class BadNamesChecker extends BugChecker implements
        BugChecker.IdentifierTreeMatcher,
        BugChecker.MethodInvocationTreeMatcher,
        BugChecker.MethodTreeMatcher {

    @java.lang.Override
    public Description matchIdentifier(IdentifierTree identifierTree, VisitorState visitorState) {
        // NOTE: This matches identifier uses. Do we want to match these,
        // or just declarations?
        Name identifier = identifierTree.getName();
        return checkName(identifierTree, identifier);
    }

    @Override
    public Description matchMethodInvocation(MethodInvocationTree methodInvocationTree, VisitorState visitorState) {
        // NOTE: Similarly to the above, this matches method names in method
        // calls. Do we want to match these, or just declarations?
        Tree methodSelect = methodInvocationTree.getMethodSelect();

        Name identifier;

        if (methodSelect instanceof MemberSelectTree) {
            identifier = ((MemberSelectTree) methodSelect).getIdentifier();
        } else if (methodSelect instanceof IdentifierTree) {
            identifier = ((IdentifierTree) methodSelect).getName();
        } else {
            throw malformedMethodInvocationTree(methodInvocationTree);
        }

        return checkName(methodInvocationTree, identifier);
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        // MethodTree represents the definition of a method. We want to check the name of this
        // method to see if it is acceptable.

        Name methodName = methodTree.getName();
        if (methodName.length() < 4) {
            return buildDescription(methodTree)
                    .setMessage(String.format("%s is too short to be a good method name", methodName))
                    .build();
        }
        if (methodName.toString().contains("_") || methodName.toString().contains("-") || methodName.toString().contains(" ")) {
            return buildDescription(methodTree)
                    .setMessage(String.format("%s is not a valid method name", methodName))
                    .build();
        }
        for (VariableTree variableTree : methodTree.getParameters()) {
            System.out.println("Checking parameter: " + variableTree.getName());
            if (variableTree.getName().contentEquals("_") || variableTree.getName().toString().contains("1")) {
                return buildDescription(methodTree)
                        .setMessage(String.format("%s is not a valid parameter name", variableTree.getName()))
                        .build();
            }
            if (variableTree.getName().length() < 3) {
                return buildDescription(methodTree)
                        .setMessage(String.format("%s is too short to be a good parameter name", variableTree.getName()))
                        .build();
            }
            if (variableTree.getName().length() > 30) {
                return buildDescription(methodTree)
                        .setMessage(String.format("%s is too long to be a good parameter name", variableTree.getName()))
                        .build();
            }
        }

        // TODO: Remove this, if needed. This is just here because we need to return a Description.
        return Description.NO_MATCH;
    }

    private Description checkName(Tree tree, Name identifier) {
        // TODO: What other names are a problem? Add checks for them here...
        if (identifier.contentEquals("foo")) {
            return buildDescription(tree)
                    .setMessage(String.format("%s is a bad identifier name", identifier))
                    .build();
        }
        if (identifier.length() < 3 && (identifier.length() == 1 && !identifier.contentEquals("i") && !identifier.contentEquals("j") && !identifier.contentEquals("k"))) {
            return buildDescription(tree)
                    .setMessage(String.format("%s is too short to be a good identifier name", identifier))
                    .build();
        }
        if (identifier.length() > 30) {
            return buildDescription(tree)
                    .setMessage(String.format("%s is too long to be a good identifier name", identifier))
                    .build();
        }

        return Description.NO_MATCH;
    }

    private static final IllegalStateException malformedMethodInvocationTree(MethodInvocationTree tree) {
        return new IllegalStateException(String.format("Method name %s is malformed.", tree));
    }
}