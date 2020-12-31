% This script solves the system described in Excercise 3.3
% First solve for n = 10
A = createMatrixA(10);
b = createVectorb(10);
gaussSeidel(A,b,10^-4)
A = createMatrixA(10000);
b = createVectorb(10000);
gaussSeidel(A,b,10^-4)


function A = createMatrixA(n)
% Crete the matrix described in the excercise
    A = zeros(n);
    for i = 1:n
        A(i,i) = 5;
        A(i+1,i) = -2;
        A(i,i+1) = -2;
    end
    A(end,end) = 5;
end

function b = createVectorb(n)
% Create the vector described in the  excercise
    b = ones(n);
    b(1) = 3;
    b(end) = 3;
end