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
        name = "LoopComplexityChecker",
        summary = "Detects excessive loop complexity in methods",
        severity = WARNING,
        linkType = CUSTOM,
        link = "https://github.com/jvc-nc/pAnalysisProject.git"
)
public class BadComplexityChecker extends BugChecker implements
        BugChecker.MethodTreeMatcher {


    public Description countLoops(MethodTree methodTree, VisitorState visitorState) {
        LoopCounter scanner = new LoopCounter();
        scanner.scan(methodTree.getBody(), null);

        int loopCount = scanner.count;

        if (loopCount > 10) {
            return buildDescription(methodTree)
                    .setMessage("Method has too many loops, cyclomatic complexity is too high: " + loopCount)
                    .build();
        } else if (loopCount > 7) {
            return buildDescription(methodTree)
                    .setMessage("Method has a high number of loops, consider refactoring: " + loopCount)
                    .build();
        }

        return Description.NO_MATCH;
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        Description loopCheck = countLoops(methodTree, visitorState);
        if (loopCheck != Description.NO_MATCH) {
            return loopCheck;
        }

        return Description.NO_MATCH;
    }

    private static class LoopCounter extends TreeScanner<Void, Void> {
        int count = 0;

        @Override
        public Void visitForLoop(ForLoopTree node, Void unused) {
            count++;
            return super.visitForLoop(node, unused);
        }

        @Override
        public Void visitWhileLoop(WhileLoopTree node, Void unused) {
            count++;
            return super.visitWhileLoop(node, unused);
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoopTree node, Void unused) {
            count++;
            return super.visitDoWhileLoop(node, unused);
        }
    }
}