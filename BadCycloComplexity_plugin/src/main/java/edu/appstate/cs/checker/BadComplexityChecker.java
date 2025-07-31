package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(
        name = "BadCyclomaticComplexity",
        summary = "Detects excessive loop complexity in methods",
        severity = WARNING,
        linkType = CUSTOM,
        link = "https://github.com/jvc-nc/pAnalysisProject.git"
)
public class BadComplexityChecker extends BugChecker implements
        BugChecker.MethodTreeMatcher {

    /**
     * Counts the number of loops in a method and returns a warning message if thresholds are exceeded.
     * @param methodTree the method tree to analyze
     * @return a warning message if thresholds are exceeded, otherwise null
     */
    public String countLoops(MethodTree methodTree) {
        LoopCounter scanner = new LoopCounter();
        scanner.scan(methodTree.getBody(), null);

        int loopCount = scanner.count;
        if (loopCount > 8) {
            return String.format(
                "Method '%s' has too many loops:\n" +
                "  Loop count        : %d\n" +
                "  Recommended max   : 5\n" +
                "  Suggestion        : Consider refactoring to reduce loops.",
                methodTree.getName(), loopCount
            );
        } else if (loopCount > 5) {
            return String.format(
                "Method '%s' has a moderately high number of loops:\n" +
                "  Loop count        : %d\n" +
                "  Recommended max   : 5",
                methodTree.getName(), loopCount
            );
        }
        return null;
    }

    /**
     * Counts the number of branches in a method and returns a warning message if thresholds are exceeded.
     * @param methodTree the method tree to analyze
     * @return a warning message if thresholds are exceeded, otherwise null
     */
    public String countBranches(MethodTree methodTree) {
        BranchCounter scanner = new BranchCounter();
        scanner.scan(methodTree.getBody(), null);

        int branchCount = scanner.count;
        if (branchCount > 12) {
            return String.format(
                "Method '%s' has too many branches:\n" +
                "  Branch count      : %d\n" +
                "  Recommended max   : 8\n" +
                "  Suggestion        : Consider refactoring to reduce branches.",
                methodTree.getName(), branchCount
            );
        } else if (branchCount > 8) {
            return String.format(
                "Method '%s' has a moderately high number of branches:\n" +
                "  Branch count      : %d\n" +
                "  Recommended max   : 8",
                methodTree.getName(), branchCount
            );
        }
        return null;
    }

    /**
     * Computes total cyclomatic complexity from loops and branches.
     * If exceeds the threshold, a warning message is returned.
     * @param methodTree the method to analyze
     * @return a warning message if total complexity exceeds, otherwise null
     */
    public String countTotalComplexity(MethodTree methodTree) {
        LoopCounter loopScanner = new LoopCounter();
        BranchCounter branchScanner = new BranchCounter();
        loopScanner.scan(methodTree.getBody(), null);
        branchScanner.scan(methodTree.getBody(), null);

        int total = loopScanner.count + branchScanner.count;
        if (total > 15) {
            return String.format(
                "Method '%s' has high total cyclomatic complexity:\n" +
                "  Combined complexity: %d\n" +
                "  Recommended max    : 15\n" +
                "  Suggestion         : Consider refactoring to simplify control flow.",
                methodTree.getName(), total
            );
        }
        return null;
    }

    /**
     * Matches method declarations and collects all complexity warnings (loops, branches, total) in a single message.
     * @param methodTree the method to analyze
     * @param visitorState the state of the visitor
     * @return a Description if any complexity threshold is violated, otherwise NO_MATCH
     */
    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        StringBuilder message = new StringBuilder();

        String loopMsg = countLoops(methodTree);
        String branchMsg = countBranches(methodTree);
        String totalMsg = countTotalComplexity(methodTree);

        if (loopMsg != null) {
            message.append(loopMsg).append("\n");
        }

        if (branchMsg != null) {
            message.append(branchMsg).append("\n");
        }

        if (totalMsg != null) {
            message.append(totalMsg).append("\n");
        }

        if (!message.isEmpty()) {
            return buildDescription(methodTree)
                    .setMessage(message.toString().trim())
                    .build();
        }

        return Description.NO_MATCH;
    }


    /**
     * Visitor class that updates the cyclomatic complexity by counting loops.
     * It counts the number of for, while, do-while, and enhanced for loops
     * in the method body and increments the cyclomatic complexity accordingly.
     */
    private static class LoopCounter extends TreeScanner<Void, Void> {
        int count = 0;

        /**
         * Counts traditional for-loops.
         */
        @Override
        public Void visitForLoop(ForLoopTree node, Void unused) {
            count++;
            return super.visitForLoop(node, unused);
        }

        /**
         * Counts while-loops.
         */
        @Override
        public Void visitWhileLoop(WhileLoopTree node, Void unused) {
            count++;
            return super.visitWhileLoop(node, unused);
        }

        /**
         * Counts do-while-loops.
         */
        @Override
        public Void visitDoWhileLoop(DoWhileLoopTree node, Void unused) {
            count++;
            return super.visitDoWhileLoop(node, unused);
        }
         /**
         * Counts enhanced for-loops (for-each).
         */
        @Override
        public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void unused) {
            count++;
            return super.visitEnhancedForLoop(node, unused);
        }
    }

    /**
     * Visitor class that counts branches in the method body.
     * It counts the number of if statements, conditional expressions,
     * switch statements, and catch blocks to determine the cyclomatic complexity.
     */
    private static class BranchCounter extends TreeScanner<Void, Void> {
        int count = 0;

        /**
         * Counts if and else-if branches.
         */
        @Override
        public Void visitIf(IfTree node, Void unused) {
            count++;
            return super.visitIf(node, unused);
        }

        /**
         * Counts ternary conditional expressions.
         */
        @Override
        public Void visitConditionalExpression(ConditionalExpressionTree node, Void unused) {
            count++;
            return super.visitConditionalExpression(node, unused);
        }

        /**
         * Counts switch case branches.
         */
        @Override
        public Void visitSwitch(SwitchTree node, Void unused) {
            count += node.getCases().size();
            return super.visitSwitch(node, unused);
        }

        /**
         * Counts catch blocks in try-catch statements.
         */
        @Override
        public Void visitCatch(CatchTree node, Void unused) {
            count++;
            return super.visitCatch(node, unused);
        }
    }
}
