function G = createMatrixGq5(q,n)
% This function creates the matrix G given in Exercise 4 Question 5
    G = zeros(n);
    A = zeros(n);
    A(1,[2 9]) = 1;
    A(2,[3 5 7]) = 1;
    A(3,[2 6 8]) = 1;
    A(4,[3 12]) = 1;
    A(5,[1,10]) = 1;
    A(6:7,10:11) = 1;
    A(8,4) = 1;
    A(8,11) = 3;
    A(9,[5 6 10]) = 1;
    A(10,13) = 1;
    A(11,15) = 1;
    A(12,11) = 3;
    A(12,[7 8 11]) = 1;
    A(13,[9 14]) = 1;
    A(14,[10 11 13 15]) = 1;
    A(15,[12 14]) = 1;
    for i = 1:n
        for j = 1:n
            nj = 0;
            for k = 1:n
                nj = nj + A(j,k);
            end
            G(i,j) = q/n + (A(j,i)*(1-q))/nj;
        end
    end
end