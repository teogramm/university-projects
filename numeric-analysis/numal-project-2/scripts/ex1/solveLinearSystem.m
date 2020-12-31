function x = solveLinearSystem(A,b)
% This function solves the system Ax=b by using LU decomposition.
% It returns a vector containing the solutions x1...xn.
    [P,L,U] = luDecomp(A);
    % We first need to solve Ly = Pb
    % Let Pb = c
    c = P*b;
    y = zeros(size(L,1),1);
    y(1) = c(1);
    for row = 2:size(L,1)
        column = 1;
        tempy = 0;
        % Keep the loop going until the rest of the row is only zeros
        % and the only element remaining is the current yn.
        while any(L(row,column+1:end))
            tempy = tempy + L(row,column)*y(column);
            column = column + 1;
        end
        tempy = (c(column)-tempy)/L(row,column);
        y(row) = tempy;
    end
    % Then we solve Ux=y
    % We need to solve this system backwards
    x = zeros(size(U,1),1);
    x(end) = y(end)/U(end,end);
    for row = size(U,1)-1:-1:1
        column = size(U,1);
        tempx = 0;
        % Keep the loop going until the rest of the row is only zeros
        % and the only element remaining is the current xn.
        while any(U(row,1:column-1))
            tempx = tempx + U(row,column)*x(column);
            column = column -1;
        end
        tempx = (y(column)-tempx)/U(row,column);
        x(row) = tempx;
    end
end
    


function [P,L,U] = luDecomp(A)
% This function takes a matrix as input and return 3 matrices
% P,L,U so that PA=LU
    aSize = size(A);
    adim = aSize(1,1);
    P = eye(adim);
    L = eye(adim);
    % currentRow == currentCol because we move along the main diagonal
    currentRow = 1;
    while currentRow<adim
        % First,  check that the row is not all zeros
        if ~any(A(currentRow,:))
            % Swap the current row with the last row
            A([currentRow end],:) = A([end currentRow],:);
            P([currentRow end],:) = P([end currentRow],:);
        end
        % Get the column that we are working on
        v = A(currentRow:end,currentRow);
        % If the maximum element is in a different position swap the lines
        maxPos = findMax(v);
        if maxPos~=1
            % The element is given relative to the current row
            % We remove 1 because if 1 is the position of the maximum then
            % we are on the same row, thus we do not need to move
            maxRow = currentRow + maxPos-1;
            A([currentRow maxRow],:) = A([maxRow currentRow],:);
            P([currentRow maxRow],:) = P([maxRow currentRow],:);
            % Update the L matrix
            for i = 1:currentRow-1
                L([currentRow maxRow],i) = L([maxRow currentRow],i);
            end
        end
        for i = currentRow+1:adim
            multiplier = -A(i,currentRow)/A(currentRow,currentRow);
            L(i,currentRow) = -multiplier;
            A(i,:) = A(i,:)+multiplier*A(currentRow,:);
        end
        currentRow = currentRow + 1;
    end
    U = triu(A);
end

function index = findMax(v)
% Takes a vector as input and finds the position of its maximum element
% (by absolute value)
    vSize = size(v);
    if(vSize(1,1)>vSize(1,2))
        c(1,:) = v(:,1);
        v = c;
    end
    vSize = size(v);
    numOfElements = vSize(1,2);
    max = abs(v(1));
    index = 1;
    for i = 1:numOfElements
        if(abs(v(1,i))>max)
            index = i;
            max = abs(v(1,i));
        end
    end
end