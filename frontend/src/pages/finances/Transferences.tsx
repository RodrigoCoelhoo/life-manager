import { useEffect, useState } from "react";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import type { PageResponseDTO } from "../../services/api.dto";
import { transferenceService } from "../../services/finances/transference/transference.service";
import React from "react";
import { Pagination } from "../../components/common/Pagination";
import { PencilSquareIcon } from "@heroicons/react/16/solid";
import { Modal } from "../../components/common/Modal";
import toast from "react-hot-toast";
import type { TransferenceDTO, TransferenceResponseDTO } from "../../services/finances/transference/transference.dto";
import TransferenceForm from "../../components/finances/TransferenceForm";
import { formatBalance } from "../../services/finances/currencies.type";
import type { WalletResponseDTO } from "../../services/finances/wallet/wallet.dto";
import TransferenceFilter from "../../components/finances/TransferenceFilter";
import { IoMdOptions } from "react-icons/io";

export interface TransferenceFilters {
	sender?: WalletResponseDTO;
	receiver?: WalletResponseDTO;
	startDate?: string;
	endDate?: string;
}

export default function Transferences() {
	const [isOpen, setIsOpen] = useState<boolean>(false);
	const [transferences, setTransferences] = useState<TransferenceResponseDTO[]>([]);
	const [activeTransference, setActiveTransference] = useState<TransferenceResponseDTO>();
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const elementsPerPage = 24;

	const [filterOpen, setFilterOpen] = useState<boolean>(false);
	const [filters, setFilters] = useState<TransferenceFilters>({});

	const fetchTransferences = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<TransferenceResponseDTO> = await transferenceService.getAllTransferences(page - 1, elementsPerPage, filters);

			setTransferences(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch transferences");
		} finally {
			setLoading(false);
		}
	};

	const createTransference = async (transference: TransferenceDTO) => {
		try {
			setLoading(true);
			const data: TransferenceResponseDTO = await transferenceService.createTransference(transference);

			setTransferences(prev => {
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

	const updateTransference = async (id: number, transference: TransferenceDTO) => {
		try {
			setLoading(true);
			const updated = await transferenceService.updateTransference(id, transference);
			setTransferences(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			toast.error("Wallet balance is insufficient!");
		} finally {
			setLoading(false);
		}
	};

	const deleteTransference = async (id: number) => {
		try {
			setLoading(true);
			await transferenceService.deleteTransference(id);
			fetchTransferences();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			toast.error("Wallet balance can't be bellow 0.00");
		} finally {
			setLoading(false);
		}
	};


	const applyFilters = (newFilters: TransferenceFilters) => {
		setPage(1);
		setFilters(newFilters);
	};

	useEffect(() => {
		fetchTransferences();
	}, [page, filters]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load transferences"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchTransferences()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-2 sm:p-6 text-textcolor flex flex-col gap-4">

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

						{filters.sender &&
							<div className="py-1 px-4 bg-textcolor/5 rounded-2xl">
								<span className="font-semibold">Sender:</span> {filters.sender.name}
							</div>
						}

						{filters.receiver &&
							<div className="py-1 px-4 bg-textcolor/5 rounded-2xl">
								<span className="font-semibold">Receiver:</span> {filters.receiver.name}
							</div>
						}
					</div>
				</div>

				<div className="h-full p-2 w-full text-textcolor text-sm rounded-t-lg grid grid-cols-[4fr_6fr_5fr_6fr_5fr_4fr] sm:grid-cols-[4fr_6fr_5fr_6fr_5fr_10fr_4fr] drop-shadow-[0px_4px_6px_rgba(0,0,0,0.2)]">

					{/* Header */}
					<div className="font-semibold bg-primary py-2 px-2 rounded-tl-lg">Date</div>
					<div className="font-semibold bg-primary py-2 px-2 wrap-break-words">Sender</div>
					<div className="font-semibold bg-primary py-2 px-2">Amount</div>
					<div className="font-semibold bg-primary py-2 px-2 wrap-break-words">Receiver</div>
					<div className="font-semibold bg-primary py-2 px-2">Amount</div>
					<div className="font-semibold bg-primary py-2 px-2 hidden sm:block">Description</div>
					<div className="font-semibold bg-primary py-2 px-2 rounded-tr-lg truncate text-center"></div>

					{/* Rows */}
					{transferences.map((item, index) => {
						const rowStyle = index % 2 === 0 ? "bg-foreground/90" : "bg-foreground/70";

						return (
							<React.Fragment key={item.id}>
								<div className={`${rowStyle} py-1 px-2 font-extralight whitespace-nowrap min-w-max`}>{new Date(item.date).toLocaleDateString()}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate`}>{item.fromWallet.name}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight text-[#F87171]`}>-{formatBalance(item.fromAmount)}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate`}>{item.toWallet.name}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight text-[#34D399]`}>+{formatBalance(item.toAmount)}</div>
								<div className={`${rowStyle} py-1 px-2 font-extralight truncate hidden sm:block`}>
									{item.description || "No description provided"}
								</div>
								<button
									className={`${rowStyle} py-1 px-2 font-extralight`}
									onClick={() => {
										setActiveTransference(item);
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
					setActiveTransference(undefined);
				}}>
				<TransferenceForm
					transference={activeTransference}
					onClose={() => {
						setIsOpen(false);
						setActiveTransference(undefined);
					}}
					onCreate={createTransference}
					onDelete={deleteTransference}
					onUpdate={updateTransference}
				/>
			</Modal>

			<Modal isOpen={filterOpen} onClose={() => setFilterOpen(false)}>
				<TransferenceFilter
					filters={filters}
					onApplyFilters={applyFilters}
					onClose={() => setFilterOpen(false)}
				/>
			</Modal>
		</>
	);
}