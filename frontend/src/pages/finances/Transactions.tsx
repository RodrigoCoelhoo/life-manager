import { useEffect, useState } from "react";
import type { TransactionDTO, TransactionResponseDTO } from "../../services/finances/transaction/transaction.dto";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import type { PageResponseDTO } from "../../services/api.dto";
import { transactionService } from "../../services/finances/transaction/transaction.service";
import React from "react";
import { Pagination } from "../../components/common/Pagination";
import { PencilSquareIcon } from "@heroicons/react/16/solid";
import { Modal } from "../../components/common/Modal";
import TransactionForm from "../../components/finances/TransactionForm";
import toast from "react-hot-toast";
import { formatBalance } from "../../services/finances/currencies.type";

export default function Transactions() {
	const [isOpen, setIsOpen] = useState<boolean>(false);
	const [transactions, setTransactions] = useState<TransactionResponseDTO[]>([]);
	const [activeTransaction, setActiveTransaction] = useState<TransactionResponseDTO>();
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const elementsPerPage = 24;

	const fetchTransactions = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<TransactionResponseDTO> = await transactionService.getTransactions(page - 1, elementsPerPage);

			setTransactions(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch exercises");
		} finally {
			setLoading(false);
		}
	};

	const createTransaction = async (transaction: TransactionDTO) => {
		try {
			setLoading(true);
			const data: TransactionResponseDTO = await transactionService.createTransaction(transaction);

			setTransactions(prev => {
				const updated = [data, ...prev].sort((a, b) => {
					const timeA = new Date(a.date).getTime();
					const timeB = new Date(b.date).getTime();

					if (timeA !== timeB) return timeB - timeA;
					return b.id - a.id;
				});

				return updated.slice(0, elementsPerPage);
			});

			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			toast.error("Wallet balance is insufficient!");
		} finally {
			setLoading(false);
		}
	};

	const updateTransaction = async (id: number, transaction: TransactionDTO) => {
		try {
			setLoading(true);
			const updated = await transactionService.updateTransaction(id, transaction);
			setTransactions(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			toast.error("Wallet balance is insufficient!");
		} finally {
			setLoading(false);
		}
	};

	const deleteTransaction = async (id: number) => {
		try {
			setLoading(true);
			await transactionService.deleteTransaction(id);
			fetchTransactions();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			toast.error("Wallet balance can't be bellow 0.00");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchTransactions();
	}, [page]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load transactions"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchTransactions()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-2 sm:p-6 text-textcolor flex flex-col gap-4">

				<div className="flex items-end justify-between gap-4 px-2">
					<button
						className="bg-primary w-fit p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
						onClick={() => setIsOpen(true)}
					>
						Create +
					</button>

					<h1 className="text-3xl text-red-400">Filters missing, by Wallet, by Date between and Category</h1>

					<span className="text-secondary"> {totalElements} {totalElements === 1 ? "transaction" : "transactions"} </span>
				</div>

				<div className="h-full p-2 w-full text-textcolor text-sm rounded-t-lg grid grid-cols-[4fr_10fr_7fr_7fr_3fr] sm:grid-cols-[4fr_10fr_7fr_20fr_7fr_3fr] drop-shadow-[0px_4px_6px_rgba(0,0,0,0.2)]">

					{/* Header */}
					<div className="font-semibold bg-primary py-2 px-2 rounded-tl-lg">Date</div>
					<div className="font-semibold bg-primary py-2 px-2 wrap-break-words">Wallet</div>
					<div className="font-semibold bg-primary py-2 px-2">Category</div>
					<div className="font-semibold bg-primary py-2 px-2 hidden sm:block">Description</div>
					<div className="font-semibold bg-primary py-2 px-2">Amount</div>
					<div className="font-semibold bg-primary py-2 px-2 rounded-tr-lg truncate text-center"></div>

					{/* Rows */}
					{transactions.map((item, index) => {
						const rowStyle = index % 2 === 0 ? "bg-foreground/90" : "bg-foreground/70";

						return (
							<React.Fragment key={item.id}>
								<div className={`${rowStyle} py-1 px-2 font-extralight whitespace-nowrap min-w-max`}>{new Date(item.date).toLocaleDateString()}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate`}>{item.wallet.name}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight`}>{item.category}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate hidden sm:block`}>
									{item.description || "No description provided"}
								</div>
								<div className={`${rowStyle} flex flex-row py-1 px-2 font-extralight ${item.type === "EXPENSE" ? "text-[#F87171]" : "text-[#34D399]"} whitespace-nowrap`}>
									<span className="inline-block w-4 text-right">
										{item.type === "EXPENSE" ? "-" : "+"}
									</span>{" "}
									{formatBalance(item.amount)}
								</div>
								<button
									className={`${rowStyle} py-1 px-2 font-extralight`}
									onClick={() => {
										setActiveTransaction(item);
										setIsOpen(true);
									}}
								>
									<PencilSquareIcon className="h-5 w-5 hover:rounded-full hover:bg-gray-400/30 p-0.5 cursor-pointer" />
								</button>
							</React.Fragment>
						);
					})}
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal
				isOpen={isOpen}
				onClose={() => {
					setIsOpen(false);
					setActiveTransaction(undefined);
				}}>
				<TransactionForm
					transaction={activeTransaction}
					onClose={() => {
						setIsOpen(false);
						setActiveTransaction(undefined);
					}}
					onCreate={createTransaction}
					onDelete={deleteTransaction}
					onUpdate={updateTransaction}
				/>
			</Modal>
		</>
	);
}