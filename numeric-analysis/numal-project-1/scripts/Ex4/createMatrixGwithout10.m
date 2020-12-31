function G = createMatrixGwithout10(q,n)
% This function creates the matrix G given in Exercise 4
    G = zeros(n);
    A = zeros(n);
    A(1,[2 9]) = 1;
    A(2,[3 5 7]) = 1;
    A(3,[2 6 8]) = 1;
    A(4,[3 11]) = 1;
    A(5,[1,9]) = 1;
    A(6:7,9:10) = 1;
    A(8,[4 10]) = 1;
    A(9,[5 6 11]) = 1;
    A(10,14) = 1;
    A(11,[7 8 10]) = 1;
    A(12,[9 13]) = 1;
    A(13,[10 12 14]) = 1;
    A(14,[11 13]) = 1;
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