import { useEffect, useState } from "react";
import type { ExpenseCategory, TransactionDTO, TransactionResponseDTO } from "../../services/finances/transaction/transaction.dto";
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
import { IoMdOptions } from "react-icons/io";
import type { WalletResponseDTO } from "../../services/finances/wallet/wallet.dto";
import TransactionFilter from "../../components/finances/TransactionFilter";
import { FaMoneyBillWave } from "react-icons/fa6";

export interface TransactionFilters {
	category?: ExpenseCategory;
	wallet?: WalletResponseDTO;
	startDate?: string;
	endDate?: string;
}

export default function Transactions() {
	const [isOpen, setIsOpen] = useState<boolean>(false);
	const [transactions, setTransactions] = useState<TransactionResponseDTO[]>([]);
	const [activeTransaction, setActiveTransaction] = useState<TransactionResponseDTO>();
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const elementsPerPage = 20;

	const [filterOpen, setFilterOpen] = useState<boolean>(false);
	const [filters, setFilters] = useState<TransactionFilters>({});

	const fetchTransactions = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<TransactionResponseDTO> = await transactionService.getTransactions(page - 1, elementsPerPage, filters);
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
	}, [page, filters]);

	const applyFilters = (newFilters: TransactionFilters) => {
		setPage(1);
		setFilters(newFilters);
	};

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
			<div className="w-full p-2 sm:p-6 text-textcolor flex flex-col gap-1 min-h-screen">

				<div className="flex flex-col gap-2 p-2">
					<div className="flex items-center justify-between gap-4">
						<div className="flex flex-row gap-2 items-center justify-center">
							<button
								className="bg-primary w-fit p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
								onClick={() => setIsOpen(true)}
							>
								Create +
							</button>

							<IoMdOptions
								className="bg-primary hover:bg-primary/80 cursor-pointer p-2 rounded-lg"
								size={38}
								onClick={() => setFilterOpen(true)}
							/>
						</div>

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "transaction" : "transactions"} </span>
					</div>

					<div className="flex flex-row flex-wrap gap-4 text-textcolor text-sm font-extralight">
						{(filters.startDate || filters.endDate) &&
							<div className="py-1 px-4 bg-textcolor/5 rounded-2xl">
								{(filters.startDate && filters.endDate) ?

									<div className="flex flex-row gap-1 flex-nowrap">
										<span className="font-semibold">From:</span> {filters.startDate || "?"}
										<span className="font-semibold">To:</span> {filters.endDate || "?"}
									</div>
									:
									filters.startDate ?
										<div><span className="font-semibold">From:</span> {filters.startDate || "?"}</div> :
										<div><span className="font-semibold">To:</span> {filters.endDate || "?"}</div>
								}
							</div>
						}

						{filters.wallet &&
							<div className="py-1 px-4 bg-textcolor/5 rounded-2xl">
								<span className="font-semibold">Wallet:</span> {filters.wallet.name}
							</div>
						}

						{filters.category &&
							<div className="py-1 px-4 bg-textcolor/5 rounded-2xl">
								<span className="font-semibold">Category:</span> {filters.category}
							</div>
						}
					</div>
				</div>

				{transactions.length === 0 ? (
					<div className="flex flex-col items-center justify-center rounded-2xl py-20 flex-1 min-h-[70vh]">
						<div className="mb-3 text-primary/70">
							<FaMoneyBillWave size={32}/>
						</div>

						<p className="text-lg font-medium text-textcolor">
							No transactions yet
						</p>

						<p className="text-sm text-textcolor/60 mt-1 mb-4">
							Create your first transaction to start tracking your finances
						</p>
					</div>
				) : (
					<div className="h-full p-2 w-full text-textcolor text-sm rounded-t-lg grid grid-cols-[4fr_10fr_7fr_7fr_3fr] sm:grid-cols-[4fr_10fr_7fr_20fr_7fr_3fr] drop-shadow-[0px_4px_6px_rgba(0,0,0,0.2)]">

						<div className="font-semibold bg-primary py-2 px-2 rounded-tl-lg">Date</div>
						<div className="font-semibold bg-primary py-2 px-2 wrap-break-words">Wallet</div>
						<div className="font-semibold bg-primary py-2 px-2">Category</div>
						<div className="font-semibold bg-primary py-2 px-2 hidden sm:block">Description</div>
						<div className="font-semibold bg-primary py-2 px-2">Amount</div>
						<div className="font-semibold bg-primary py-2 px-2 rounded-tr-lg truncate text-center"></div>

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
				)}

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

			<Modal isOpen={filterOpen} onClose={() => setFilterOpen(false)}>
				<TransactionFilter
					filters={filters}
					onApplyFilters={applyFilters}
					onClose={() => setFilterOpen(false)}
				/>
			</Modal>
		</>
	);
}